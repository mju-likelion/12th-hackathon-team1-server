package com.hackathonteam1.refreshrator.dto.response.ingredient;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class IngredientDto {

    private UUID id;
    private String name;

    public static IngredientDto changeToDto(Ingredient ingredient){
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }

}
