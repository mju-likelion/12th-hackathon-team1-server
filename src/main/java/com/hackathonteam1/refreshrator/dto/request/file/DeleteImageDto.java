package com.hackathonteam1.refreshrator.dto.request.file;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteImageDto {
    @NotNull
    private UUID imageId;
    @NotNull
    private UUID recipeId;
}
