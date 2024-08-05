package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Image;
import com.hackathonteam1.refreshrator.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findByRecipe(Recipe recipe);
}
