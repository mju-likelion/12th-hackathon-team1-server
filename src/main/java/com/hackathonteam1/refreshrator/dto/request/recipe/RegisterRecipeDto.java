package com.hackathonteam1.refreshrator.dto.request.recipe;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class RegisterRecipeDto {

    @Size(min = 1, max = 20)
    @NotBlank
    private String name;

    @NotNull
    private List<UUID> ingredientIds;

    @Size(min = 1)
    @NotBlank
    private String cookingStep;

}
