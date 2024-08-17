package com.hackathonteam1.refreshrator.dto.request.recipe;

import com.hackathonteam1.refreshrator.exception.BadRequestException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class RegisterIngredientRecipesDto {
    @NotNull
    private List<UUID> ingredientIds;

    // 재료Id 리스트 중 중복되는 Id가 없다면
    public List<UUID> nonDupIngredientIds(){
        List<UUID> nonDupIngredientIds = this.getIngredientIds().stream().distinct().collect(Collectors.toList());
        if(this.ingredientIds.size() != nonDupIngredientIds.size()){
            throw new BadRequestException(ErrorCode.DUPLICATED_RECIPE_INGREDIENT);
        }
        return nonDupIngredientIds;
    }
}
