package com.hackathonteam1.refreshrator.repository;

import com.hackathonteam1.refreshrator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmailAndPassword(String email,String password);
    Boolean existsByEmail(String email);
}
