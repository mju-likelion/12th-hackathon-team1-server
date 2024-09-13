package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.DeleteIngredientRecipesDto;
import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterIngredientRecipesDto;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.BadRequestException;
import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.ForbiddenException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.IngredientRecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IngredientRecipeServiceImpl implements IngredientRecipeService{
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UserService userService;

    private final IngredientRecipeRepository ingredientRecipeRepository;

    //레시피에 재료들 등록
    public void registerIngredients(List<UUID> ingredientIds, Recipe recipe){
        ingredientIds.forEach(ingredientId->
                registerRecipeIngredient(ingredientService.findIngredientById(ingredientId), recipe));
    }

    //요청에 중복된 재료가 있는지 확인.
    public void checkDuplicatedIngredient(List<UUID> ingredientIds){
        Set<UUID> ingredientIdSet = new HashSet<>(ingredientIds);
        if(ingredientIdSet.size() != ingredientIds.size()){
            throw new BadRequestException(ErrorCode.DUPLICATED_RECIPE_INGREDIENT);
        }
    }

    //레시피 재료 등록
    @Transactional
    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
    public void registerIngredientRecipe(User user, UUID recipeId, RegisterIngredientRecipesDto registerIngredientRecipesDto) {
        Recipe recipe = recipeService.findRecipeById(recipeId);

        if(!userService.isAuthorized(recipe.getUser(), user)){ //레피시 작성자 여부 확인
            throw new ForbiddenException(ErrorCode.RECIPE_FORBIDDEN);
        }

        //기존에 레시피에 존재하던 재료 리스트, contains 여부 속도 향상을 위해 Set을 사용
        Set<Ingredient> existingIngredients = new HashSet<>(findAllIngredientByIngredientRecipes(findAllIngredientRecipeByRecipe(recipe)));

        List<UUID> requestedIngredientIds = registerIngredientRecipesDto.getIngredientIds();
        //요청된 재료에 중복이 있는지 확인
        checkDuplicatedIngredient(requestedIngredientIds);

        List<Ingredient> newIngredients = requestedIngredientIds.stream().map(ingredientService::findIngredientById).toList();

        //기존에 레시피에 존재하던 재료인지 확인 후 추가
        newIngredients.forEach(newIngredient->{
            if(existingIngredients.contains(newIngredient)){
                throw new ConflictException(ErrorCode.RECIPE_INGREDIENT_CONFLICT);
            }
            registerRecipeIngredient(newIngredient, recipe);
        });
    }

    //레시피 재료 삭제
    @CacheEvict(value = "recipeDetailCache", key = "#recipeId", cacheManager = "redisCacheManager")
    public void deleteIngredientRecipe(User user, UUID recipeId, DeleteIngredientRecipesDto deleteIngredientRecipesDto) {
        Recipe recipe = recipeService.findRecipeById(recipeId);

        if(!userService.isAuthorized(recipe.getUser(), user)){ //레피시 작성자 여부 확인
            throw new ForbiddenException(ErrorCode.RECIPE_FORBIDDEN);
        }

        //기존 레시피의 재료들
        List<IngredientRecipe> existingIngredientRecipes = findAllIngredientRecipeByRecipe(recipe);
        //기존 레시피의 재료들의 Id를 파싱해서 가져옴
        Set<UUID> existingIngredientRecipeIds = existingIngredientRecipes.stream().map(i->i.getId()).collect(Collectors.toSet());

        List<UUID>requestedIngredientIds = deleteIngredientRecipesDto.getIngredientRecipeIds();

        //요청된 재료 중복 확인
        checkDuplicatedIngredient(requestedIngredientIds);

        requestedIngredientIds.forEach(i -> {
            if(!existingIngredientRecipeIds.contains(i)){
                throw new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND);
            }
            ingredientRecipeRepository.deleteById(i);
        });

    }

    //RecipeIngredient를 등록하는 메서드
    private void registerRecipeIngredient(Ingredient ingredient, Recipe recipe){
        IngredientRecipe ingredientRecipe = IngredientRecipe.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .build();
        ingredientRecipeRepository.save(ingredientRecipe);
    }

    //IngredientRecipe 리스트로 Ingredient 리스트 생성하는 메서드
    private List<Ingredient> findAllIngredientByIngredientRecipes(List<IngredientRecipe> ingredientRecipes){
        return ingredientRecipes.stream().map(i->i.getIngredient()).collect(Collectors.toList());
    }

    //Recipe로 해당 Recipe 내에 존재하는 IngredientRecipe 리스트를 반환하는 메서드
    private List<IngredientRecipe> findAllIngredientRecipeByRecipe(Recipe recipe){
        return ingredientRecipeRepository.findAllByRecipe(recipe).orElseThrow(()-> new NotFoundException(ErrorCode.INGREDIENT_RECIPE_NOT_FOUND));
    }

}
