package com.hackathonteam1.refreshrator.dto.request.recipe;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class RegisterRecipeDto {

    @Size(min = 1, max = 20)
    private String name;

    @NotNull
    private List<UUID> ingredientIds;

    @Size(min = 1)
    private String cookingStep;

}
