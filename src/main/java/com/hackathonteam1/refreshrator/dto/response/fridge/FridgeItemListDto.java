package com.hackathonteam1.refreshrator.dto.response.fridge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FridgeItemListDto {
    private List<FridgeItemDto> coldStorage; // 냉장
    private List<FridgeItemDto> frozen; // 냉동
    private List<FridgeItemDto> ambient; // 실온
    private List<FridgeItemDto> ExpirationDate; // 유통기한 만료
}
