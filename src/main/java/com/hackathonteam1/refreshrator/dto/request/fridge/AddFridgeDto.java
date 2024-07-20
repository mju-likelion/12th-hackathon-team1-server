package com.hackathonteam1.refreshrator.dto.request.fridge;

import com.hackathonteam1.refreshrator.entity.FridgeItem;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddFridgeDto {

    //재료
    @NotBlank
    private UUID ingredient;

    //유통기한 설정
    @NotBlank
    private LocalDate expiredDate;

    //수량 설정
    @NotBlank
    private int quantity;

    //보관 방법
    @NotBlank
    private String storage;

    //메모
    private String memo;

}
