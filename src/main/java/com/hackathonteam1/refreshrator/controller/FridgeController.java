package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.fridge.AddFridgeDto;
import com.hackathonteam1.refreshrator.dto.response.fridge.FridgeItemListDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.FridgeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/fridge")
public class FridgeController {

    private final FridgeService fridgeService;

    //냉장고에 재료 추가
    @PostMapping("/ingredients")
    public ResponseEntity<ResponseDto<Void>> addIngredientInFridge(@RequestBody @Valid AddFridgeDto addFridgeDto, @AuthenticatedUser User user) {
        fridgeService.addIngredientInFridge(addFridgeDto, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED, "냉장고에 재료 등록 성공"), HttpStatus.CREATED);
    }

    //냉장고에 재료 정보 수정
    @PatchMapping("/ingredients/{ingredient_id}")
    public ResponseEntity<ResponseDto<Void>> updateIngredientInFridge(@PathVariable("ingredient_id") UUID id , @RequestBody @Valid AddFridgeDto addFridgeDto, @AuthenticatedUser User user) {
        fridgeService.updateIngredientInFridge(id,addFridgeDto, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "냉장고에 재료 수정 성공"), HttpStatus.OK);
    }

    //냉장고에 재료 삭제
    @DeleteMapping("/ingredients/{ingredient_id}")
    public ResponseEntity<ResponseDto<Void>> deleteIngredientInFridge(@PathVariable("ingredient_id") UUID id , @AuthenticatedUser User user) {
        fridgeService.deleteIngredientInFridge(id, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "냉장고에 재료 삭제 성공"), HttpStatus.OK);
    }

    // 냉장고에 있는 모든 재료 조회
    @GetMapping("/ingredients")
    public ResponseEntity<ResponseDto<FridgeItemListDto>> getIngredientsInFridge(@AuthenticatedUser User user) {
        FridgeItemListDto fridgeItemListDto = fridgeService.getIngredientsInFridge(user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "냉장고에 있는 모든 재료 조회 성공", fridgeItemListDto), HttpStatus.OK);
    }

}
