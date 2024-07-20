package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.fridge.AddFridgeDto;
import com.hackathonteam1.refreshrator.entity.Fridge;
import com.hackathonteam1.refreshrator.entity.FridgeItem;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.FridgeItemRepository;
import com.hackathonteam1.refreshrator.repository.FridgeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FridgeService {
    private IngredientRepository ingredientRepository;
    private FridgeItemRepository fridgeItemRepository;
    private FridgeRepository fridgeRepository;

    //냉장고에 재료 추가
    public void addIngredientInFridge(AddFridgeDto addFridgeDto, User user){

        //재료 찾기
        Ingredient ingredient=ingredientRepository.findById(addFridgeDto.getIngredientId())
                .orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_NOT_FOUND));

        //냉장고 찾기
        Fridge fridge=fridgeRepository.findByUser(user)
                .orElseThrow(()-> new NotFoundException(ErrorCode.FRIDGE_NOT_FOUND));

        //재료 정보 입력
        FridgeItem fridgeItem=FridgeItem.builder()
                .fridge(fridge)
                .ingredient(ingredient)
                .expiredDate(addFridgeDto.getExpiredDate())
                .quantity(addFridgeDto.getQuantity())
                .storage(defindStorage(addFridgeDto.getStorage()))
                .memo(addFridgeDto.getMemo())
                .build();

        //재료를 냉장고에 저장
        fridgeItemRepository.save(fridgeItem);
    }

    private FridgeItem.Storage defindStorage(String storage){
        return switch (storage){
            case "상온"-> FridgeItem.Storage.STORE_AT_ROOM_TEMPERATURE;
            case "냉동"-> FridgeItem.Storage.FROZEN;
            default -> FridgeItem.Storage.REFRIGERATED;
        };
    }
}
