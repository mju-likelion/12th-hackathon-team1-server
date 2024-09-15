package com.hackathonteam1.refreshrator.dto.response.recipeLike;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecipeLikedDataList {
    private final List<RecipeLikedData> recipeLikedDataList;
    public static RecipeLikedDataList of(List<RecipeLikedData> recipeLikedData){
        return new RecipeLikedDataList(recipeLikedData);
    }
}
