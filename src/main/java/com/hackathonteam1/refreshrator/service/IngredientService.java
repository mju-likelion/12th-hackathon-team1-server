package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientDto;
import com.hackathonteam1.refreshrator.dto.response.ingredient.IngredientListDto;
import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientListDto showAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<IngredientDto> ingredientDtoList = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            IngredientDto ingredientDto = IngredientDto.builder()
                    .id(ingredient.getId())
                    .name(ingredient.getName())
                    .build();
            ingredientDtoList.add(ingredientDto);
        }
        return new IngredientListDto(ingredientDtoList);
    }
}
