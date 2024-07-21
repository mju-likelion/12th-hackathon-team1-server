package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.IngredientRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
import com.hackathonteam1.refreshrator.entity.User;

import java.util.UUID;

public interface RecipeService {

    //레시피 등록
    public void register(RegisterRecipeDto registerRecipeDto, User user);

    //레시피 상세조회
    public DetailRecipeDto getDetail(UUID recipeId);

    public void modifyContent(RecipeDto recipeDto, User user, UUID recipeId);

    public void registerIngredientRecipe(User user, UUID recipeId, IngredientRecipeDto ingredientRecipeDto);

}
