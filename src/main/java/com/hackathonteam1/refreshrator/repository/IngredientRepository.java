package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
}
