package com.hackathonteam1.refreshrator.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RecipeResponseDto {
    private UUID recipeId; // 레시피 Id
    private String name; // 레시피 이름
}
