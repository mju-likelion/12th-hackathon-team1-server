package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.DeleteIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterIngredientRecipesDto;
import com.hackathonteam1.refreshrator.entity.User;

import java.util.UUID;

public interface IngredientRecipeService {

    //레시피 재료 추가
    public void registerIngredientRecipe(User user, UUID recipeId, RegisterIngredientRecipesDto registerIngredientRecipesDto);

    //레시피 재료 삭제
    public void deleteIngredientRecipe(User user, UUID recipeId, DeleteIngredientRecipesDto deleteIngredientRecipesDto);
}
