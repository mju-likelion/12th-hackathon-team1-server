package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientDto;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class IngredientRecipeResponseDto {

    private UUID id;
    private IngredientDto ingredient;

    public static IngredientRecipeResponseDto changeToDto(IngredientRecipe ingredientRecipe){
        return new IngredientRecipeResponseDto(ingredientRecipe.getId(), IngredientDto.changeToDto(ingredientRecipe.getIngredient()));
    }

}
