package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import com.hackathonteam1.refreshrator.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    Page<Recipe> findAllByNameContaining(String keyword, Pageable pageable);

    @Query("SELECT r FROM recipe r JOIN r.ingredientRecipes ir JOIN ir.ingredient i WHERE i IN :ingredients GROUP BY r.id HAVING COUNT(i) >= :match OR COUNT(i) = SIZE(r.ingredientRecipes)")
    Page<Recipe> findAllByIngredientRecipesContain(@Param("ingredients") Set<Ingredient> ingredients, @Param("match") int match, Pageable pageable);
    Page<Recipe> findAll(Pageable pageable);
}
