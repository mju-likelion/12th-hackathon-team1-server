package com.hackathonteam1.refreshrator.dto.request.recipe;

import com.hackathonteam1.refreshrator.exception.BadRequestException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class DeleteIngredientRecipesDto {

    @NotNull
    private List<UUID> ingredientRecipeIds;

    public List<UUID> nonDupIngredientIds(){
        List<UUID> nonDupIngredientIds = this.getIngredientRecipeIds().stream().distinct().collect(Collectors.toList());
        if(this.getIngredientRecipeIds().size() != nonDupIngredientIds.size()){
            throw new BadRequestException(ErrorCode.DUPLICATED_RECIPE_INGREDIENT);
        }
        return nonDupIngredientIds;
    }
}
