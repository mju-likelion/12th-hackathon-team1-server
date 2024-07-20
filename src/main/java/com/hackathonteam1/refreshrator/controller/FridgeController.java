package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.fridge.AddFridgeDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.FridgeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/fridge")
public class FridgeController {

    private final FridgeService fridgeService;

    //냉장고에 재료 추가
    @PostMapping("/ingredients")
    public ResponseEntity<ResponseDto<Void>> addIngredientInFridge(@RequestBody AddFridgeDto addFridgeDto , @AuthenticatedUser User user){
        fridgeService.addIngredientInFridge(addFridgeDto,user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED,"냉장고에 재료 등록 성공"),HttpStatus.CREATED);
    }
}
