package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
