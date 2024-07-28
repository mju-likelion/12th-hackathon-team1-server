package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecipeDto {

    private UUID recipeId;
    private String name;
    private int likeCount;
    private ImageDto image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public static RecipeDto mapping(Recipe recipe){
        return new RecipeDto(recipe.getId(), recipe.getName(), recipe.getLikeCount(),
                ImageDto.mapping(recipe.getImage()), recipe.getCreatedAt(), recipe.getUpdatedAt());
    }
}
