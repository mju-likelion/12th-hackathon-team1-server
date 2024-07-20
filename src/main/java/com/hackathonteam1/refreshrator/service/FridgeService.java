package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.fridge.AddFridgeDto;
import com.hackathonteam1.refreshrator.entity.Fridge;
import com.hackathonteam1.refreshrator.entity.FridgeItem;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.ForbiddenException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.FridgeItemRepository;
import com.hackathonteam1.refreshrator.repository.FridgeRepository;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FridgeService {
    private IngredientRepository ingredientRepository;
    private FridgeItemRepository fridgeItemRepository;
    private FridgeRepository fridgeRepository;

    //냉장고에 재료 추가
    public void addIngredientInFridge(AddFridgeDto addFridgeDto, User user){

        //재료 찾기
        Ingredient ingredient=findIngredient(addFridgeDto);

        //냉장고 찾기
        Fridge fridge=findFridge(user);

        //재료 정보 입력
        FridgeItem fridgeItem=FridgeItem.builder()
                .fridge(fridge)
                .ingredient(ingredient)
                .user(user)
                .expiredDate(addFridgeDto.getExpiredDate())
                .quantity(addFridgeDto.getQuantity())
                .storage(defindStorage(addFridgeDto.getStorage()))
                .memo(addFridgeDto.getMemo())
                .build();

        //재료를 냉장고에 저장
        fridgeItemRepository.save(fridgeItem);
    }

    public void updateIngredientInFridge(UUID id, AddFridgeDto addFridgeDto, User user){

        //수정할 재료 찾기
        FridgeItem fridgeItem=fridgeItemRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ErrorCode.FRIDGE_ITEM_NOT_FOUND));

        //유저가 등록한 재료인지 검사
        checkAuth(user,fridgeItem.getUser());

        //수정하기
        fridgeItem.setExpiredDate(addFridgeDto.getExpiredDate());
        fridgeItem.setQuantity(addFridgeDto.getQuantity());
        fridgeItem.setStorage(defindStorage(addFridgeDto.getStorage()));
        fridgeItem.setMemo(addFridgeDto.getMemo());

        //저장하기
        fridgeItemRepository.save(fridgeItem);
    }

    //저장방법 결정 메서드
    private FridgeItem.Storage defindStorage(String storage){
        return switch (storage){
            case "상온"-> FridgeItem.Storage.STORE_AT_ROOM_TEMPERATURE;
            case "냉동"-> FridgeItem.Storage.FROZEN;
            default -> FridgeItem.Storage.REFRIGERATED;
        };
    }

    //데이터베이스에서 재료id로 재료를 찾는 메서드
    private Ingredient findIngredient(AddFridgeDto addFridgeDto){
        return ingredientRepository.findById(addFridgeDto.getIngredientId())
                .orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_NOT_FOUND));
    }

    //데이터베이스에서 유저의 냉장고를 찾는 메서드
    private Fridge findFridge(User user){
        return fridgeRepository.findByUser(user)
                .orElseThrow(()-> new NotFoundException(ErrorCode.FRIDGE_NOT_FOUND));
    }

    //권한 확인
    private void checkAuth(User writer, User user){
        if(!writer.getId().equals(user.getId())){
            throw new ForbiddenException(ErrorCode.FRIDGE_ITEM_FORBIDDEN);
        }
    }
}
