package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> register(
            @RequestBody RegisterRecipeDto registerRecipeDto, @AuthenticatedUser User user){
        recipeService.register(registerRecipeDto, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK,"레시피 등록 성공"),HttpStatus.OK);
    }

}
