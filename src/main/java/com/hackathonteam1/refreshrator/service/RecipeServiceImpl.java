package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.ModifyRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
import com.hackathonteam1.refreshrator.entity.*;

import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.entity.Image;

import com.hackathonteam1.refreshrator.exception.*;

import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.*;
import com.hackathonteam1.refreshrator.repository.FridgeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRecipeRepository;
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
    private final ImageService imageService;
    private final IngredientRecipeServiceImpl ingredientRecipeServiceImpl;
    private final UserService userService;
    private final FridgeService fridgeService;

    private final RecipeRepository recipeRepository;
    private final IngredientRecipeRepository ingredientRecipeRepository;
    private final RecipeLikeRepository recipeLikeRepository;

    private final S3Uploader s3Uploader;

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

        List<UUID> ingredientIds = registerRecipeDto.getIngredientIds();

        //동일한 재료를 요청할 경우 예외처리
        ingredientRecipeServiceImpl.checkDuplicatedIngredient(ingredientIds);

        Recipe recipe = Recipe.builder()
                .name(registerRecipeDto.getName())
                .cookingStep(registerRecipeDto.getCookingStep())
                .user(user)
                .image(image)
                .build();

        recipeRepository.save(recipe);

        ingredientRecipeServiceImpl.registerIngredients(ingredientIds, recipe);
    }

    //상세조회
    @Override
    @Cacheable(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
    public DetailRecipeDto getDetail(UUID recipeId) {
        Recipe recipe = findRecipeById(recipeId);
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
        Recipe recipe = findRecipeById(recipeId);
        if(!userService.isAuthorized(recipe.getUser(), user)){ //레피시 작성자 여부 확인
            throw new ForbiddenException(ErrorCode.RECIPE_FORBIDDEN);
        }

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
        Recipe recipe = findRecipeById(recipeId);

        if(!userService.isAuthorized(recipe.getUser(), user)){ //레피시 작성자 여부 확인
            throw new ForbiddenException(ErrorCode.RECIPE_FORBIDDEN);
        }
        if(recipe.isContainingImage()){
            Image image = imageService.findImageByRecipe(recipe);
            s3Uploader.removeS3FileByUrl(image.getUrl());
        }
        recipeRepository.delete(recipe);
    }

    //추천 레시피 조회
    @Override
    public RecipeListDto getRecommendation(int page, int size, int match, String type, User user) {
        Set<FridgeItem> userFridgeItems = fridgeService.findFridge(user).getFridgeItem().stream()
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
    @Override
    public RecipeListDto showAllLikedRecipes(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<RecipeLike> recipeLikes = this.recipeLikeRepository.findAllByUser(user, pageable);
        List<Recipe> recipes = recipeLikes.stream().map(like -> like.getRecipe()).collect(Collectors.toList());
        Page<Recipe> recipePage = new PageImpl<>(recipes);

        checkValidPage(recipePage, page);

        RecipeListDto recipeListDto = RecipeListDto.mapping(recipePage);
        return recipeListDto;
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
        Recipe recipe = findRecipeById(recipeId);

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
        Recipe recipe = findRecipeById(recipeId);
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

    public Recipe findRecipeById(UUID recipeId){
        return recipeRepository.findById(recipeId).orElseThrow(()-> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
    }

    //Recipe로 해당 Recipe 내에 존재하는 IngredientRecipe 리스트를 반환하는 메서드
    private List<IngredientRecipe> findAllIngredientRecipeByRecipe(Recipe recipe){
        return ingredientRecipeRepository.findAllByRecipe(recipe).orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND));
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

}
