package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeDto {

    private String name;
    private int likeCount;
    public static RecipeDto mapping(Recipe recipe){
        return new RecipeDto(recipe.getName(), recipe.getLikeCount());
    }
}
