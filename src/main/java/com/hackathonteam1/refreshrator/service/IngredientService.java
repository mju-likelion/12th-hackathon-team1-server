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

        List<IngredientDto> ingredientDtoList = ingredients.stream()
                .filter(ingredient -> ingredient.getName().contains(name))
                .map(IngredientDto::changeToDto)
                .toList();

        return new IngredientListDto(ingredientDtoList);
    }
}
