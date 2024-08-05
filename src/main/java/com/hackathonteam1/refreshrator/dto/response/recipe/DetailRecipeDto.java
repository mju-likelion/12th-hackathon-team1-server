package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.entity.Image;
import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class DetailRecipeDto {
    private UUID id;
    private String name;
    private List<IngredientRecipeResponseDto> ingredientRecipes;
    private String cookingStep;
    private int likeCount;
    private ImageDto image;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static DetailRecipeDto mapping(Recipe recipe, List<IngredientRecipe> ingredientRecipes){
        List<IngredientRecipeResponseDto> ingredientDtos = ingredientRecipes.stream().map(
                i->IngredientRecipeResponseDto.changeToDto(i)).collect(Collectors.toList());
        return new DetailRecipeDto(recipe.getId(), recipe.getName(), ingredientDtos,
                recipe.getCookingStep(), recipe.getLikeCount(), ImageDto.mapping(recipe.getImage()),
                recipe.getCreatedAt(), recipe.getUpdatedAt());
    }
}
