package com.hackathonteam1.refreshrator.dto.response.recipeLike;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecipeLikedData {
    private final UUID recipeId;
    private final boolean liked;

    public static RecipeLikedData of(UUID recipeId, boolean liked){
        return new RecipeLikedData(recipeId, liked);
    }
}
