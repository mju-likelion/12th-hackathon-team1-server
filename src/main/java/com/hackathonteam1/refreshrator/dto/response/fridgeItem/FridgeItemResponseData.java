package com.hackathonteam1.refreshrator.dto.response.fridgeItem;

import com.hackathonteam1.refreshrator.entity.FridgeItem;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class FridgeItemResponseData {

    private String ingredientName;
    private LocalDate expiredDate;
    private Integer quantity;
    private FridgeItem.Storage storage;
    private String memo;

    public static FridgeItemResponseData fromFridgeItem(FridgeItem fridgeItem) {
        return FridgeItemResponseData.builder()
                .ingredientName(fridgeItem.getIngredient().getName())
                .expiredDate(fridgeItem.getExpiredDate())
                .quantity(fridgeItem.getQuantity())
                .storage(fridgeItem.getStorage())
                .memo(fridgeItem.getMemo())
                .build();
    }
}
