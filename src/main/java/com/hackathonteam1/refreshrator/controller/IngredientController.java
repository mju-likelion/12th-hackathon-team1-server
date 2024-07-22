package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientListDto;
import com.hackathonteam1.refreshrator.service.IngredientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    // DB에 있는 재료 전체 조회
    @GetMapping()
    public ResponseEntity<ResponseDto<IngredientListDto>> showAllIngredients() {
        IngredientListDto ingredientListDto = ingredientService.showAllIngredients();
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "모든 재료 조회 성공", ingredientListDto), HttpStatus.OK);
    }

    // DB에 있는 재료 검색
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<IngredientListDto>> searchIngredientByName(@RequestParam String name) {
        IngredientListDto ingredientListDto = ingredientService.searchIngredientByName(name);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "재료 검색 성공", ingredientListDto), HttpStatus.OK);
    }
}
