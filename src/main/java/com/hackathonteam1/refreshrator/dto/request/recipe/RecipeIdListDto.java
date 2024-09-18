package com.hackathonteam1.refreshrator.dto.request.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecipeIdListDto {
    @NotNull
    private List<UUID> recipeIds;
}
