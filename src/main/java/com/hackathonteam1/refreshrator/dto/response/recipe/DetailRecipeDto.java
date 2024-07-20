package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientDto;
import com.hackathonteam1.refreshrator.entity.Ingredient;
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
    private List<IngredientDto> ingredients;
    private String cookingStep;

    public static DetailRecipeDto detailRecipeDto(String name, List<Ingredient> ingredients, String cookingStep){
        List<IngredientDto> ingredientDtos = ingredients.stream().map(
                i->IngredientDto.changeToDto(i)).collect(Collectors.toList());
        return new DetailRecipeDto(name, ingredientDtos, cookingStep);
    }
}
