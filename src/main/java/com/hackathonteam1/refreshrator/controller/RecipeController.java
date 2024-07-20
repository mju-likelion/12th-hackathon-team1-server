package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.ModifyRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> register(
            @RequestBody @Valid RegisterRecipeDto registerRecipeDto, @AuthenticatedUser User user){
        recipeService.register(registerRecipeDto, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED,"레시피 등록 성공"),HttpStatus.CREATED);
    }

    @GetMapping("/{recipe_id}")
    public ResponseEntity<ResponseDto<DetailRecipeDto>> getDetail(@PathVariable("recipe_id") UUID recipeId){
        DetailRecipeDto detailRecipeDto = recipeService.getDetail(recipeId);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "레시피 상세 조회 성공", detailRecipeDto),HttpStatus.OK);
    }

    @PatchMapping("/{recipe_id}")
    public ResponseEntity<ResponseDto<Void>> modify(
            @RequestBody @Valid ModifyRecipeDto modifyRecipeDto, @AuthenticatedUser User user, @PathVariable("recipe_id") UUID recipeId){
        recipeService.modifyContent(modifyRecipeDto, user, recipeId);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK,"레시피 수정 성공"),HttpStatus.OK);
    }


}
