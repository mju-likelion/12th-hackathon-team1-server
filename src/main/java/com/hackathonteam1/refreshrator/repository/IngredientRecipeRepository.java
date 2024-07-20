package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.IngredientRecipe;
import com.hackathonteam1.refreshrator.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, UUID> {
    Optional<List<IngredientRecipe>> findAllByRecipe(Recipe recipe);
}
