package com.hackathonteam1.refreshrator.dto.response.ingredient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class IngredientListDto {
    private List<IngredientDto> ingredients;
}
