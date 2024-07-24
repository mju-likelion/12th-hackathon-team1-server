package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.DeleteIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.ModifyRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<ResponseDto<RecipeListDto>> getList(@RequestParam(name = "keyword",defaultValue = "")String keyword, @RequestParam(name = "type", defaultValue = "newest")String type,
                                                              @RequestParam(name = "page", defaultValue = "0")int page, @RequestParam(name = "size", defaultValue = "10")int size){
        RecipeListDto recipeListDto = recipeService.getList(keyword, type, page, size);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK,"레시피 목록 조회 성공", recipeListDto),HttpStatus.OK);
    }

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

    @DeleteMapping("/{recipe_id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable("recipe_id") UUID recipeId, @AuthenticatedUser User user){
        recipeService.delete(recipeId, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK,"레시피 삭제 성공"),HttpStatus.OK);
    }

    @PostMapping("/{recipe_id}/ingredients")
    public ResponseEntity<ResponseDto<Void>> registerIngredientRecipe(@PathVariable("recipe_id") UUID recipeId,
                                                                      @RequestBody @Valid RegisterIngredientRecipesDto registerIngredientRecipesDto, @AuthenticatedUser User user){
        recipeService.registerIngredientRecipe(user, recipeId, registerIngredientRecipesDto);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED, "레시피 재료 등록 성공"),HttpStatus.CREATED);
    }

    @DeleteMapping("/{recipe_id}/ingredients")
    public ResponseEntity<ResponseDto<Void>> deleteIngredientRecipe(@PathVariable("recipe_id") UUID recipeId,
                                                                    @RequestBody @Valid DeleteIngredientRecipesDto deleteIngredientRecipesDto, @AuthenticatedUser User user){
        recipeService.deleteIngredientRecipe(user, recipeId, deleteIngredientRecipesDto);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "레시피 재료 삭제 성공"),HttpStatus.OK);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ResponseDto<RecipeListDto>> getRecommendations(
            @RequestParam(name = "page", defaultValue = "0")int page,
            @RequestParam(name = "size", defaultValue = "10")int size,
            @RequestParam(name = "match", defaultValue = "2147483647")int match,
            @RequestParam(name = "type", defaultValue = "newest")String type,
            @AuthenticatedUser User user){
        RecipeListDto recipeListDto = recipeService.getRecommendation(page, size, match, type, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK,"추천 레시피 목록 조회 성공", recipeListDto),HttpStatus.OK);
    }

    // 레시피에 좋아요 추가
    @PostMapping("/{recipe_id}/like")
    public ResponseEntity<ResponseDto<Void>> addLikeToRecipe(@PathVariable("recipe_id") UUID recipeId, @AuthenticatedUser User user){
        recipeService.addLikeToRecipe(user, recipeId);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED, "레시피에 좋아요 추가 성공"),HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<ResponseDto<ImageDto>> registerFile(
            @RequestPart MultipartFile file){
        ImageDto imageDto =  recipeService.registerImage(file);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "이미지 등록 성공", imageDto),HttpStatus.OK);
    }

    @DeleteMapping(value = "/images/{image_Id}")
    public ResponseEntity<ResponseDto<Void>> deleteFile(
            @PathVariable UUID image_Id,@AuthenticatedUser User user){
        recipeService.deleteImage(image_Id, user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "이미지 삭제 성공"),HttpStatus.OK);

    }

}
