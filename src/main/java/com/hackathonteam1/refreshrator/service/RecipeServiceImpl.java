package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
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


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public DetailRecipeDto getDetail(UUID recipeId) {
        Recipe recipe = findRecipeByRecipeId(recipeId);
        List<IngredientRecipe> ingredientRecipes = findAllIngredientRecipeByRecipe(recipe);

        List<Ingredient> ingredients = ingredientRecipes.stream().map(i->i.getIngredient()).collect(Collectors.toList());

        DetailRecipeDto detailRecipeDto = DetailRecipeDto.detailRecipeDto(recipe.getName(),ingredients, recipe.getCookingStep());

        return detailRecipeDto;
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

    private Recipe findRecipeByRecipeId(UUID recipeId){
        return recipeRepository.findById(recipeId).orElseThrow(()-> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
    }

    private List<IngredientRecipe> findAllIngredientRecipeByRecipe(Recipe recipe){
        return ingredientRecipeRepository.findAllByRecipe(recipe).orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND));
    }
}
