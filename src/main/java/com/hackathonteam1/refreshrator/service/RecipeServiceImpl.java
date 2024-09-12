package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.DeleteIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.ModifyRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeDto;
import com.hackathonteam1.refreshrator.entity.*;

import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.entity.Fridge;
import com.hackathonteam1.refreshrator.entity.Image;

import com.hackathonteam1.refreshrator.exception.*;

import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.*;
import com.hackathonteam1.refreshrator.repository.FridgeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRecipeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import com.hackathonteam1.refreshrator.repository.RecipeRepository;
import com.hackathonteam1.refreshrator.util.S3Uploader;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeServiceImpl implements RecipeService{
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientRecipeRepository ingredientRecipeRepository;
    private final FridgeRepository fridgeRepository;
    private final S3Uploader s3Uploader;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ImageService imageService;

    //레시피 목록 조회
    @Override
    @Cacheable(value = "recipeListCache",key = "#keyword + '-' + #type + '-' + #page + '-' + #size", cacheManager = "redisCacheManager")
    public RecipeListDto getList(String keyword, String type, int page, int size) {

        Sort sort = determineSortStrategy(type);

        Pageable pageable = PageRequest.of(page, size, sort);

        if(keyword.equals("")){
            Page<Recipe> recipePage = recipeRepository.findAll(pageable);
            checkValidPage(recipePage, page);
            return RecipeListDto.mapping(recipePage);
        }

        Page<Recipe> recipePage = recipeRepository.findAllByNameContaining(keyword, pageable);
        checkValidPage(recipePage, page);
        return RecipeListDto.mapping(recipePage);
    }

    //레시피 등록
    @Override
    @Transactional
    @CacheEvict(value = "recipeListCache", allEntries = true, cacheManager = "redisCacheManager")
    public void register(RegisterRecipeDto registerRecipeDto, User user) {
        Image image = null;

        if(registerRecipeDto.getImageId()!=null){
            image = imageService.findImageById(registerRecipeDto.getImageId());
        }

        //동일한 재료를 요청할 경우 예외처리
        checkDuplicatedIngredient(registerRecipeDto.getIngredientIds());

        Recipe recipe = Recipe.builder()
                .name(registerRecipeDto.getName())
                .cookingStep(registerRecipeDto.getCookingStep())
                .user(user)
                .image(image)
                .build();

        recipeRepository.save(recipe);

        registerRecipeDto.getIngredientIds().stream().forEach(i ->
                registerRecipeIngredient(findIngredientByIngredientId(i), recipe));
    }

    //상세조회
    @Override
    @Cacheable(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
    public DetailRecipeDto getDetail(UUID recipeId) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        List<IngredientRecipe> ingredientRecipes = findAllIngredientRecipeByRecipe(recipe);

        DetailRecipeDto detailRecipeDto = DetailRecipeDto.mapping(recipe, ingredientRecipes);
        return detailRecipeDto;
    }

    //레시피 정보 수정
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "recipeListCache", allEntries = true, cacheManager = "redisCacheManager"),
                    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
            }
    )
    @Transactional
    public void modifyContent(ModifyRecipeDto modifyRecipeDto, User user, UUID recipeId) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(), user);

        if(modifyRecipeDto.getDeleteImageId()!=null){
            if(!recipe.getImage().getId().equals(modifyRecipeDto.getDeleteImageId())){
                throw new BadRequestException(ErrorCode.NOT_IMAGE_OF_RECIPE);
            }
            recipe.deleteImage();
        }
        if(modifyRecipeDto.getImageId()!=null){
            recipe.updateImage(imageService.findImageById(modifyRecipeDto.getImageId()));
        }
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
    @Caching(
            evict = {
                    @CacheEvict(value = "recipeListCache", allEntries = true, cacheManager = "redisCacheManager"),
                    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
            }
    )
    public void delete(UUID recipeId, User user) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(), user);
        if(recipe.isContainingImage()){
            Image image = imageService.findImageByRecipe(recipe);
            s3Uploader.removeS3FileByUrl(image.getUrl());
        }
        recipeRepository.delete(recipe);
    }

    //레시피 재료 등록
    @Override
    @Transactional
    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
    public void registerIngredientRecipe(User user, UUID recipeId, RegisterIngredientRecipesDto registerIngredientRecipesDto) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(), user);

        //기존에 레시피에 존재하던 재료 리스트, 탐색 속도 향상을 위해 Set을 사용
        Set<Ingredient> existingIngredients = new HashSet<>(findAllIngredientByIngredientRecipes(findAllIngredientRecipeByRecipe(recipe)));

        List<UUID> requestedIngredientIds = registerIngredientRecipesDto.getIngredientIds();
        //요청된 재료에 중복이 있는지 확인
        checkDuplicatedIngredient(requestedIngredientIds);

        List<Ingredient> newIngredients = requestedIngredientIds.stream().map(i->
                findIngredientByIngredientId(i)).collect(Collectors.toList());

        //기존에 레시피에 존재하던 재료인지 확인 후 추가
        newIngredients.stream().forEach(newIngredient->{
            if(existingIngredients.contains(newIngredient)){
                throw new ConflictException(ErrorCode.RECIPE_INGREDIENT_CONFLICT);
            }
            registerRecipeIngredient(newIngredient, recipe);
        });
    }

    //레시피 재료 삭제
    @Override
    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
    public void deleteIngredientRecipe(User user, UUID recipeId, DeleteIngredientRecipesDto deleteIngredientRecipesDto) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        checkAuth(recipe.getUser(),user);

        //기존 레시피의 재료들
        List<IngredientRecipe> existingIngredientRecipes = findAllIngredientRecipeByRecipe(recipe);
        //기존 레시피의 재료들의 Id를 파싱해서 가져옴
        Set<UUID> existingIngredientRecipeIds = existingIngredientRecipes.stream().map(i->i.getId()).collect(Collectors.toSet());

        List<UUID>requestedIngredientIds = deleteIngredientRecipesDto.getIngredientRecipeIds();

        //요청된 재료 중복 확인
        checkDuplicatedIngredient(requestedIngredientIds);

        requestedIngredientIds.forEach(i -> {
            if(!existingIngredientRecipeIds.contains(i)){
                throw new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND);
            }
            ingredientRecipeRepository.deleteById(i);
        });

    }

    //추천 레시피 조회
    @Override
    public RecipeListDto getRecommendation(int page, int size, int match, String type, User user) {
        Set<FridgeItem> userFridgeItems = findFridgeByUser(user).getFridgeItem().stream()
                .filter(fridgeItem -> !fridgeItem.isExpired())
                .collect(Collectors.toSet());

        Set<Ingredient> usersIngredients = userFridgeItems.stream().map(i -> i.getIngredient()).collect(Collectors.toSet());

        Sort sort = determineSortStrategy(type);

        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Recipe> resultPages = recipeRepository.findAllByIngredientRecipesContain(usersIngredients, match, pageable);
        checkValidPage(resultPages, page);
        RecipeListDto recipeListDto = RecipeListDto.mapping(resultPages);
        return recipeListDto;
    }

    //내가 작성한 레시피 목록 조회
    @Override
    public RecipeListDto findMyRecipes(User user, String type, int page, int size) {

        Sort sort = determineSortStrategy(type);

        Pageable pageable = PageRequest.of(page,size, sort);
        Page<Recipe> recipePage = recipeRepository.findAllByUser(user, pageable);
        checkValidPage(recipePage, page);

        RecipeListDto recipeListDto = RecipeListDto.mapping(recipePage);

        return recipeListDto;
    }

    // 좋아요 누른 레시피 목록 조회
    public RecipeListDto showAllLikedRecipes(User user, int page, int size) {
        List<RecipeDto> recipeLists = new ArrayList<>();

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page, size);

        Page<RecipeLike> recipeLikes = this.recipeLikeRepository.findAllByUser(user, pageable);
        List<Recipe> recipes = recipeLikes.stream().map(like -> like.getRecipe()).collect(Collectors.toList());
        Page<Recipe> recipePage = new PageImpl<>(recipes);

        checkValidPage(recipePage, page);

        RecipeListDto recipeListDto = RecipeListDto.mapping(recipePage);
        return recipeListDto;
    }


    private Fridge findFridgeByUser(User user){
        return fridgeRepository.findByUser(user).orElseThrow(()-> new NotFoundException(ErrorCode.FRIDGE_NOT_FOUND));
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

    // 레시피에 좋아요 추가
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "recipeListCache", allEntries = true, cacheManager = "redisCacheManager"),
                    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
            }
    )
    public void addLikeToRecipe(User user, UUID recipeId){
        Recipe recipe = findRecipeByRecipeId(recipeId);

        // 유저가 이미 좋아요를 누른 레시피인지 확인
        this.isUserAlreadyAddLike(user, recipe);

        // 좋아요를 누른 레시피가 아니라면 좋아요 추가
        RecipeLike recipeLike = new RecipeLike(user, recipe);
        recipe.getRecipeLikes().add(recipeLike);
        this.recipeRepository.save(recipe);
    }

    // 레시피에 좋아요 삭제
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "recipeListCache", allEntries = true, cacheManager = "redisCacheManager"),
                    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
            }
    )
    public void deleteLikeFromRecipe(User user, UUID recipeId){
        Recipe recipe = findRecipeByRecipeId(recipeId);
        // 해당 레시피에서 내가 누른 좋아요 반환
        RecipeLike recipeLike = this.findMyRecipeLike(user, recipe);
        recipe.getUser().getRecipeLikes().remove(recipeLike);
        recipe.getRecipeLikes().remove(recipeLike);
        this.recipeLikeRepository.delete(recipeLike);
    }

    // 유저가 이미 좋아요를 누른 레시피인지 확인
    public void isUserAlreadyAddLike(User user, Recipe recipe){
        for (RecipeLike recipeLike : recipe.getRecipeLikes()) {
            if(recipeLike.getUser().getId().equals(user.getId())){
                throw new ConflictException(ErrorCode.USER_ALREADY_ADD_LIKE);
            }
        }
    }

    // 해당 레시피에서 내가 누른 좋아요 반환
    public RecipeLike findMyRecipeLike(User user, Recipe recipe){
        for (RecipeLike recipeLike : recipe.getRecipeLikes()) {
            if(recipeLike.getUser().getId().equals(user.getId())){
                return recipeLike;
            }
        }
        throw new NotFoundException(ErrorCode.RECIPE_LIKE_NOT_FOUND);
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

    private <T> void checkValidPage(Page<T> pages, int page){
        if(pages.getTotalPages() <= page && page != 0){
            throw new NotFoundException(ErrorCode.PAGE_NOT_FOUND);
        }
    }

    private Sort determineSortStrategy(String type){
        if (type.equals("newest")){
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        if (type.equals("popularity")){
            return Sort.by(Sort.Order.desc("likeCount"));
        }
        throw new BadRequestException(ErrorCode.SORT_TYPE_ERROR);
    }

    private void checkDuplicatedIngredient(List<UUID> ingredientIds){
        Set<UUID> ingredientIdSet = new HashSet<>(ingredientIds);
        if(ingredientIdSet.size() != ingredientIds.size()){
            throw new BadRequestException(ErrorCode.DUPLICATED_RECIPE_INGREDIENT);
        }
    }

}
