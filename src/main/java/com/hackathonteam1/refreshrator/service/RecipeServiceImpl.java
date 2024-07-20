package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.IngredientRecipeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import com.hackathonteam1.refreshrator.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientRecipeRepository ingredientRecipeRepository;

    @Override
    @Transactional
    public void register(RegisterRecipeDto registerRecipeDto, User user) {

        System.out.println(user.getEmail());
        Recipe recipe = Recipe.builder()
                .name(registerRecipeDto.getName())
                .cookingStep(registerRecipeDto.getCookingStep())
                .user(user)
                .build();

        recipeRepository.save(recipe);
        registerRecipeDto.getIngredientIds().stream().forEach(i -> registerRecipeIngredient(findIngredientByIngredientId(i),recipe));
    }
    private Ingredient findIngredientByIngredientId(UUID ingredientId){
        return ingredientRepository.findById(ingredientId).orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_NOT_FOUND));
    }
    private void registerRecipeIngredient(Ingredient ingredient, Recipe recipe){
        IngredientRecipe ingredientRecipe = IngredientRecipe.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .build();
        ingredientRecipeRepository.save(ingredientRecipe);
    }
}
