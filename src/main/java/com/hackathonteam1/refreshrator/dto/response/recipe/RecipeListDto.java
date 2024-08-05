package com.hackathonteam1.refreshrator.dto.response.recipe;

import com.hackathonteam1.refreshrator.dto.response.PaginationDto;
import com.hackathonteam1.refreshrator.entity.Recipe;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
public class RecipeListDto {
    private List<RecipeDto> recipeList;
    private PaginationDto pagination;

    public static RecipeListDto mapping(Page<Recipe> page){
        return RecipeListDto.builder()
                .recipeList(page.stream()
                        .map((i-> RecipeDto.mapping(i)))
                        .collect(Collectors.toList()))
                .pagination(PaginationDto.paginationDto(page))
                .build();
    }
}
