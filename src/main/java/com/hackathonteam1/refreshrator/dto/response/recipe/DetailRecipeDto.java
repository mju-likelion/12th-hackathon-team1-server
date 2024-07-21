package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class DetailRecipeDto {
    private String name;
    private List<IngredientRecipeResponseDto> ingredientRecipes;
    private String cookingStep;

    public static DetailRecipeDto detailRecipeDto(String name, List<IngredientRecipe> ingredientRecipes, String cookingStep){
        List<IngredientRecipeResponseDto> ingredientDtos = ingredientRecipes.stream().map(
                i->IngredientRecipeResponseDto.changeToDto(i)).collect(Collectors.toList());
        return new DetailRecipeDto(name, ingredientDtos, cookingStep);
    }
}
