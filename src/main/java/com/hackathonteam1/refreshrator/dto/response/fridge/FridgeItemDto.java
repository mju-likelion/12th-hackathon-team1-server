package com.hackathonteam1.refreshrator.dto.response.fridge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.hackathonteam1.refreshrator.entity.FridgeItem;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FridgeItemDto {
    private UUID id;
    private String ingredientName;
    private UUID ingredientId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    public static FridgeItemDto changeToDto(FridgeItem fridgeItem){
        return FridgeItemDto.builder()
                .id(fridgeItem.getId())
                .ingredientName(fridgeItem.getIngredient().getName())
                .ingredientId(fridgeItem.getIngredient().getId())
                .expirationDate(fridgeItem.getExpiredDate())
                .build();
    }
}
