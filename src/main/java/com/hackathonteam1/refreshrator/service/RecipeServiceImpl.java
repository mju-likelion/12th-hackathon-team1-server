package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.DeleteIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.ModifyRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
import com.hackathonteam1.refreshrator.entity.*;
import com.hackathonteam1.refreshrator.exception.*;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.ImageRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRecipeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import com.hackathonteam1.refreshrator.repository.RecipeRepository;
import com.hackathonteam1.refreshrator.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientRecipeRepository ingredientRecipeRepository;
    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public void register(RegisterRecipeDto registerRecipeDto, User user) {

        Recipe recipe = Recipe.builder()
                .name(registerRecipeDto.getName())
                .cookingStep(registerRecipeDto.getCookingStep())
                .user(user)
                .build();

        recipeRepository.save(recipe);
        registerRecipeDto.getIngredientIds().stream().forEach(i -> registerRecipeIngredient(findIngredientByIngredientId(i),recipe));
    }

    //상세조회
    @Override
    public DetailRecipeDto getDetail(UUID recipeId) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        List<IngredientRecipe> ingredientRecipes = findAllIngredientRecipeByRecipe(recipe);

        DetailRecipeDto detailRecipeDto = DetailRecipeDto.detailRecipeDto(recipe.getName(),ingredientRecipes, recipe.getCookingStep());
        return detailRecipeDto;
    }

    //레시피명, 조리법 수정
    @Override
    public void modifyContent(ModifyRecipeDto modifyRecipeDto, User user, UUID recipeId) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(), user);

        if(modifyRecipeDto.getName()!=null){
            recipe.updateName(modifyRecipeDto.getName());
        }
        if(modifyRecipeDto.getCookingStep()!=null) {
            recipe.updateCookingStep(modifyRecipeDto.getCookingStep());
        }

        recipeRepository.save(recipe);
    }

    //레시피 삭제
    @Override
    public void delete(UUID recipeId, User user) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(), user);
        recipeRepository.delete(recipe);
    }

    //레시피 재료 등록
    @Override
    @Transactional
    public void registerIngredientRecipe(User user, UUID recipeId, RegisterIngredientRecipesDto registerIngredientRecipesDto) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(), user);

        //기존에 레시피에 존재하던 재료 리스트
        List<Ingredient> ingredients = findAllIngredientByIngredientRecipes(findAllIngredientRecipeByRecipe(recipe));

        HashSet<Ingredient> existingIngredients = new HashSet<>(ingredients);

        List<Ingredient> newIngredients = registerIngredientRecipesDto.nonDupIngredientIds().stream().map(i->
                findIngredientByIngredientId(i)).collect(Collectors.toList());

        //기존에 레시피에 존재하던 재료인지 확인 후 추가
        newIngredients.stream().forEach(newIngredient->{
            if(existingIngredients.contains(newIngredient)){
                throw new ConflictException(ErrorCode.RECIPE_INGREDIENT_CONFLICT);
            }
            registerRecipeIngredient(newIngredient, recipe);
        });
    }

    @Override
    public void deleteIngredientRecipe(User user, UUID recipeId, DeleteIngredientRecipesDto deleteIngredientRecipesDto) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(),user);

        //기존 레시피의 재료들
        List<IngredientRecipe> existingIngredientRecipes = findAllIngredientRecipeByRecipe(recipe);

        Set<UUID> existingIngredientRecipeIds = existingIngredientRecipes.stream().map(i->i.getId()).collect(Collectors.toSet());

        deleteIngredientRecipesDto.nonDupIngredientIds().forEach(i -> {
            if(!existingIngredientRecipeIds.contains(i)){
                throw new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND);
            }
            ingredientRecipeRepository.deleteById(i);
        });

    }

    @Override
    public ImageDto registerImage(MultipartFile file) {

        if(!file.getContentType().equals(MediaType.IMAGE_GIF_VALUE) &&
                !file.getContentType().equals(MediaType.IMAGE_PNG_VALUE) &&
                !file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) ){
            throw new FileStorageException(ErrorCode.FILE_TYPE_ERROR);
        }

        String url;
        try {
           url = s3Uploader.upload(file);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.FILE_STORAGE_ERROR, e.getMessage());
        }

        Image image = Image.builder()
                .url(url)
                .build();

        imageRepository.save(image);
        ImageDto imageDto = ImageDto.mapping(image);
        return imageDto;
    }

    @Override
    public void deleteImage(UUID imageId, User user) {
        Image image = findImageByImageId(imageId);
        Recipe recipe = image.getRecipe();

        if(!recipe.isContainingImage()){
            throw new BadRequestException(ErrorCode.IMAGE_NOT_IN_RECIPE);
        }

        checkAuth(recipe.getUser(), user);
        recipe.deleteImage();
        s3Uploader.removeS3File(image.getUrl().split("/")[3]);
        imageRepository.delete(image);
    }

    private Image findImageByImageId(UUID imageId){
        return imageRepository.findById(imageId).orElseThrow(()->new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    private Ingredient findIngredientByIngredientId(UUID ingredientId){
        return ingredientRepository.findById(ingredientId).orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_NOT_FOUND));
    }

    //RecipeIngredient를 등록하는 메서드
    private void registerRecipeIngredient(Ingredient ingredient, Recipe recipe){
        IngredientRecipe ingredientRecipe = IngredientRecipe.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .build();
        ingredientRecipeRepository.save(ingredientRecipe);
    }

    private Recipe findRecipeByRecipeId(UUID recipeId){
        return recipeRepository.findById(recipeId).orElseThrow(()-> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
    }

    //IngredientRecipe 리스트로 Ingredient 리스트 생성하는 메서드
    private List<Ingredient> findAllIngredientByIngredientRecipes(List<IngredientRecipe> ingredientRecipes){
        return ingredientRecipes.stream().map(i->i.getIngredient()).collect(Collectors.toList());
    }

    //Recipe로 해당 Recipe 내에 존재하는 IngredientRecipe 리스트를 반환하는 메서드
    private List<IngredientRecipe> findAllIngredientRecipeByRecipe(Recipe recipe){
        return ingredientRecipeRepository.findAllByRecipe(recipe).orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND));
    }

    //권한 확인
    private void checkAuth(User writer, User user){
        if(!writer.getId().equals(user.getId())){
            throw new ForbiddenException(ErrorCode.RECIPE_FORBIDDEN);
        }
    }
}
