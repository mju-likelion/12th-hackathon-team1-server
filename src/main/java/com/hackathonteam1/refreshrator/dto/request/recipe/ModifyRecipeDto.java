package com.hackathonteam1.refreshrator.dto.request.recipe;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyRecipeDto {

    @Size(min = 1, max = 15)
    private String name;

    @Size(max = 5000)
    private String cookingStep;

    private UUID imageId;
}
