package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.Fridge;
import com.hackathonteam1.refreshrator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FridgeRepository extends JpaRepository<Fridge, UUID> {
    Optional<Fridge> findByUser(User user);
}
