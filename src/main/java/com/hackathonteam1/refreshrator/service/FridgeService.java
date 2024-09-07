package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.fridge.AddFridgeDto;
import com.hackathonteam1.refreshrator.dto.response.fridge.FridgeItemDto;
import com.hackathonteam1.refreshrator.dto.response.fridge.FridgeItemListDto;
import com.hackathonteam1.refreshrator.dto.response.fridgeItem.FridgeItemResponseData;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FridgeService {
    private IngredientRepository ingredientRepository;
    private FridgeItemRepository fridgeItemRepository;
    private FridgeRepository fridgeRepository;

    //냉장고에 재료 추가
    @CacheEvict(value = "userIngredientsCache", key = "#user.getId()", cacheManager = "redisCacheManager")
    public void addIngredientInFridge(AddFridgeDto addFridgeDto, User user){

        //재료 찾기
        Ingredient ingredient=findIngredient(addFridgeDto);

        //냉장고 찾기
        Fridge fridge=findFridge(user);

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

    //냉장고에 재료 정보 수정 메서드
    @CacheEvict(value = "userIngredientsCache", key = "#user.getId()", cacheManager = "redisCacheManager")
    public void updateIngredientInFridge(UUID fridgeItemId, AddFridgeDto addFridgeDto, User user){

        //수정할 재료 찾기
        FridgeItem fridgeItem=findFridgeItem(fridgeItemId);

        //유저가 등록한 재료인지 검사
        checkAuth(fridgeItem.getFridge().getUser(),user);

        //수정하기
        fridgeItem.setExpiredDate(addFridgeDto.getExpiredDate());
        fridgeItem.setQuantity(addFridgeDto.getQuantity());
        fridgeItem.setStorage(defindStorage(addFridgeDto.getStorage()));
        fridgeItem.setMemo(addFridgeDto.getMemo());

        //저장하기
        fridgeItemRepository.save(fridgeItem);
    }

    //냉장고에 재료 삭제 메서드
    @CacheEvict(value = "userIngredientsCache", key = "#user.getId()", cacheManager = "redisCacheManager")
    public void deleteIngredientInFridge(UUID fridgeItemId, User user){
        //삭제할 재료 찾기
        FridgeItem fridgeItem=findFridgeItem(fridgeItemId);

        //권한 확인
        checkAuth(fridgeItem.getFridge().getUser(),user);

        //삭제하기
        fridgeItemRepository.delete(fridgeItem);
    }

    // 냉장고에 모든 재료 조회
    @Cacheable(value = "userIngredientsCache",key = "#user.getId()", cacheManager = "redisCacheManager")
    public FridgeItemListDto getIngredientsInFridge(User user) {
        // 유저의 냉장고 찾기
        Fridge fridge = findFridge(user); // user를 통해 fridge를 반환

        List<FridgeItemDto> coldStorageList = new ArrayList<>(); // 냉장 재료 리스트
        List<FridgeItemDto> frozenStorageList = new ArrayList<>(); // 냉동 재료 리스트
        List<FridgeItemDto> ambientStorageList = new ArrayList<>(); // 실온 재료 리스트
        List<FridgeItemDto> expirationDateList = new ArrayList<>(); // 유통 기한 만료 재료 리스트

        setIngredientsInFridgeList(fridge.getFridgeItem(), coldStorageList, frozenStorageList, ambientStorageList, expirationDateList);

        return new FridgeItemListDto(coldStorageList, frozenStorageList, ambientStorageList, expirationDateList);
    }

    private void setIngredientsInFridgeList(List<FridgeItem> fridgeItems,
                                            List<FridgeItemDto> coldStorageList,
                                            List<FridgeItemDto> frozenStorageList,
                                            List<FridgeItemDto> ambientStorageList,
                                            List<FridgeItemDto> expirationDateList){
        for(FridgeItem fridgeItem : fridgeItems){
            FridgeItemDto fridgeItemDto = FridgeItemDto.changeToDto(fridgeItem);

            if(fridgeItem.isExpired()){
                expirationDateList.add(fridgeItemDto);
                continue;
            }
            switch (fridgeItem.getStorage()){
                case STORE_AT_ROOM_TEMPERATURE -> ambientStorageList.add(fridgeItemDto);
                case REFRIGERATED -> coldStorageList.add(fridgeItemDto);
                case FROZEN -> frozenStorageList.add(fridgeItemDto);
            }

        }
    }

    //냉장고에 있는 재료 단건 조회 메서드
    public FridgeItemResponseData detailIngredientInFridge(UUID fridgeItemId, User user){
        //조회할 재료 찾기
        FridgeItem fridgeItem=findFridgeItem(fridgeItemId);

        //권한 확인
        checkAuth(fridgeItem.getFridge().getUser(),user);

        //조회 하기
        return FridgeItemResponseData.fromFridgeItem(fridgeItem);
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

    //냉장고에서 요청한 재료를 찾는 메서드
    private FridgeItem findFridgeItem(UUID id){
        return fridgeItemRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ErrorCode.FRIDGE_ITEM_NOT_FOUND));
    }

    //권한 확인
    private void checkAuth(User writer, User user){
        if(!writer.getId().equals(user.getId())){
            throw new ForbiddenException(ErrorCode.FRIDGE_ITEM_FORBIDDEN);
        }
    }
}
