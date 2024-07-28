package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.FridgeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FridgeItemRepository extends JpaRepository<FridgeItem, UUID> {
}
