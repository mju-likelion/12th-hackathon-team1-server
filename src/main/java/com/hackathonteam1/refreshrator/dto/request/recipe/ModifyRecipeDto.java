package com.hackathonteam1.refreshrator.dto.request.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyRecipeDto {

    @Size(min = 1, max = 20)
    private String name;

    @Size(min = 1)
    private String cookingStep;
}
