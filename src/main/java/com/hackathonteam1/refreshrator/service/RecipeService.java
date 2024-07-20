package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.recipe.RegisterRecipeDto;
import com.hackathonteam1.refreshrator.entity.User;
import org.springframework.stereotype.Service;

public interface RecipeService {

    public void register(RegisterRecipeDto registerRecipeDto, User user);

}
