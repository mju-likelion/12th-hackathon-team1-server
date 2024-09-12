package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final RecipeService recipeService;

    @GetMapping("/me/recipes")
    public ResponseEntity<ResponseDto<RecipeListDto>> getMyRecipe(@AuthenticatedUser User user,
                                                                  @RequestParam(name = "type", defaultValue = "newest") String type,
                                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                                  @RequestParam(name = "size", defaultValue = "10") int size){
        RecipeListDto recipeListDto = recipeService.findMyRecipes(user, type, page, size);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "자신이 작성한 레시피 목록 조회 성공", recipeListDto),HttpStatus.OK);
    }

    // 좋아요 누른 레시피 목록 조회
    @GetMapping("/me/likes")
    public ResponseEntity<ResponseDto<RecipeListDto>> showAllRecipeLikes(@AuthenticatedUser User user,
                                                                         @RequestParam(name = "page", defaultValue = "0")int page,
                                                                         @RequestParam(name = "size", defaultValue = "10")int size) {
        RecipeListDto recipeListDto = recipeService.showAllLikedRecipes(user, page, size);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "좋아요 누른 레시피 목록 조회 성공", recipeListDto), HttpStatus.OK);
    }


}
