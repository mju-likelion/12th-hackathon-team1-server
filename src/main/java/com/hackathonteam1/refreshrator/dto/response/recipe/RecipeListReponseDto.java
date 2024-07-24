package com.hackathonteam1.refreshrator.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RecipeListReponseDto {
    private List<RecipeResponseDto> recipeResponseDtoList;
}
