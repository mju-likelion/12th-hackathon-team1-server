package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientDto;
import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientListDto;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientListDto searchIngredientByName(String name) {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<IngredientDto> ingredientDtoList = new ArrayList<>();

        for(Ingredient ingredient : ingredients) {
            if(ingredient.getName().contains(name)) { // 해당 검색어를 포함하는 모든 재료를 찾는다.
                IngredientDto ingredientDto = IngredientDto.builder()
                        .id(ingredient.getId())
                        .name(ingredient.getName())
                        .build();
                ingredientDtoList.add(ingredientDto);
            }
        }
        if(ingredientDtoList.isEmpty()) {
            throw new NotFoundException(ErrorCode.INGREDIENT_NOT_FOUND);
        }
        return new IngredientListDto(ingredientDtoList);
    }
}
