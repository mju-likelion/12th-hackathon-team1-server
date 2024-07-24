package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.DeleteIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.ModifyRecipeDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.DetailRecipeDto;

import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface RecipeService {

    //레시피 목록 조회
    public RecipeListDto getList(String keyword, String type, int page, int size);

    //레시피 등록
    public void register(RegisterRecipeDto registerRecipeDto, User user);

    //레시피 상세조회
    public DetailRecipeDto getDetail(UUID recipeId);

    //레시피 내용 수정
    public void modifyContent(ModifyRecipeDto modifyRecipeDto, User user, UUID recipeId);

    //레시피 삭제
    public void delete(UUID recipeId, User user);

    //레시피 재료 추가
    public void registerIngredientRecipe(User user, UUID recipeId, RegisterIngredientRecipesDto registerIngredientRecipesDto);

    //레시피 재료 삭제
    public void deleteIngredientRecipe(User user, UUID recipeId, DeleteIngredientRecipesDto deleteIngredientRecipesDto);

    // 레시피에 좋아요 추가
    public void addLikeToRecipe(User user, UUID recipeId);

    // 레시피에 좋아요 삭제
  
    //파일(이미지) 등록
    public ImageDto registerImage(MultipartFile file);

    //파일(이미지) 삭제
    public void deleteImage(UUID imageId, User user);

}
