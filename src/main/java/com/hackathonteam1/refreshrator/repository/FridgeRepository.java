package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FridgeRepository extends JpaRepository<Fridge, UUID> {
}
