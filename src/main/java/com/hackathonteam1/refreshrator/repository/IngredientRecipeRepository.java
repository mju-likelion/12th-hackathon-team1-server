package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, UUID> {
}
