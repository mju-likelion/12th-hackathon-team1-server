package com.hackathonteam1.refreshrator.dto.response.fridge;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
public class FridgeItemDto {
    private UUID id;
    private String ingredientName;
    private UUID ingredientId;
    private LocalDate expirationDate;
}
