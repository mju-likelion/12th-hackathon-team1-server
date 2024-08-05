package com.hackathonteam1.refreshrator.dto.response.ingredient;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class IngredientListDto {
    private List<IngredientDto> ingredients;
}
