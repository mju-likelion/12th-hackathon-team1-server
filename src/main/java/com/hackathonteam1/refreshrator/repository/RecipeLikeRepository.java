package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, UUID> {

}

