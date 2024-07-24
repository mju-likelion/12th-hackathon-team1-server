package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    Page<Recipe> findAllByNameContaining(String keyword, Pageable pageable);
    Page<Recipe> findAll(Pageable pageable);
}
