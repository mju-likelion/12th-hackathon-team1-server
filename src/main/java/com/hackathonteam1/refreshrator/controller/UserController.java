package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.annotation.PageNumber;
import com.hackathonteam1.refreshrator.annotation.PageSize;
import com.hackathonteam1.refreshrator.annotation.TypeStrategy;
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

import static com.hackathonteam1.refreshrator.constant.ParameterDefaultValue.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final RecipeService recipeService;

    @GetMapping("/me/recipes")
    public ResponseEntity<ResponseDto<RecipeListDto>> getMyRecipe(@AuthenticatedUser User user,
                                                                  @TypeStrategy @RequestParam(name = "type", defaultValue = DEFAULT_TYPE_STRATEGY) String type,
                                                                  @PageNumber @RequestParam(name = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                                  @PageSize @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size){
        RecipeListDto recipeListDto = recipeService.findMyRecipes(user, type, page, size);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "자신이 작성한 레시피 목록 조회 성공", recipeListDto),HttpStatus.OK);
    }

    // 좋아요 누른 레시피 목록 조회
    @GetMapping("/me/likes")
    public ResponseEntity<ResponseDto<RecipeListDto>> showAllRecipeLikes(@AuthenticatedUser User user,
                                                                         @PageNumber @RequestParam(name = "page", defaultValue = DEFAULT_PAGE_NUMBER)int page,
                                                                         @PageSize @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE)int size) {
        RecipeListDto recipeListDto = recipeService.showAllLikedRecipes(user, page, size);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "좋아요 누른 레시피 목록 조회 성공", recipeListDto), HttpStatus.OK);
    }


}
