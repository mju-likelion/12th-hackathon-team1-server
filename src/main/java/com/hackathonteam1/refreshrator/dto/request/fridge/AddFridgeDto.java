package com.hackathonteam1.refreshrator.dto.request.fridge;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddFridgeDto {

    //재료
    @NotNull
    private UUID ingredientId;

    //유통기한 설정
    @NotNull
    private LocalDate expiredDate;

    //수량 설정
    @NotNull
    private Integer quantity;

    //보관 방법
    @NotNull
    private String storage;

    //메모
    private String memo;

}
