package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.*;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;

import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.dto.response.recipeLike.RecipeLikedDataList;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;

import java.util.UUID;

public interface RecipeService {

    //레시피 목록 조회
    public RecipeListDto getList(String keyword, String type, int page, int size);

    //레시피 등록
    public void register(RegisterRecipeDto registerRecipeDto, User user);

    //레시피 상세조회
    public DetailRecipeDto getDetail(UUID recipeId);

    //레시피 내용 수정
    public void modifyContent(ModifyRecipeDto modifyRecipeDto, User user, UUID recipeId);

    //레시피 삭제
    public void delete(UUID recipeId, User user);

    //추천 레시피 목록 조회
    public RecipeListDto getRecommendation(int page, int size, int match, String type, User user);

    // 레시피에 좋아요 추가
    public void addLikeToRecipe(User user, UUID recipeId);

    // 레시피에 좋아요 삭제
    public void deleteLikeFromRecipe(User user, UUID recipeId);

    //자신이 작성한 레시피 조회
    public RecipeListDto findMyRecipes(User user, String type, int page, int size);

    //좋아요 누른 레시피 목록 조회
    public RecipeListDto showAllLikedRecipes(User user, int page, int size);

    //레피시 id를 통해 레시피를 찾기
    public Recipe findRecipeById(UUID recipeId);

    //레시피에 재료 추가
    public void registerIngredientRecipe(User user, UUID recipeId, RegisterIngredientRecipesDto registerIngredientRecipesDto);

    //레시피에서 재료 삭제
    public void deleteIngredientRecipe(User user, UUID recipeId, DeleteIngredientRecipesDto deleteIngredientRecipesDto);

    //레시피 목록에 대한 좋아요 여부 반환
    public RecipeLikedDataList getRecipesLiked(RecipeIdListDto recipeIdListDto, User user);
}
