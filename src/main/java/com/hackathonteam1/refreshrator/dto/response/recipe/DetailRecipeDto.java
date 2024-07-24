package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.entity.Image;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class DetailRecipeDto {
    private UUID id;
    private String name;
    private List<IngredientRecipeResponseDto> ingredientRecipes;
    private String cookingStep;
    private ImageDto image;

    public static DetailRecipeDto mapping(Recipe recipe, List<IngredientRecipe> ingredientRecipes){
        List<IngredientRecipeResponseDto> ingredientDtos = ingredientRecipes.stream().map(
                i->IngredientRecipeResponseDto.changeToDto(i)).collect(Collectors.toList());
        return new DetailRecipeDto(recipe.getId(), recipe.getName(), ingredientDtos,
                recipe.getCookingStep(),ImageDto.mapping(recipe.getImage()));
    }
}
