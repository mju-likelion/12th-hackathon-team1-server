package com.hackathonteam1.refreshrator.repository;


import com.hackathonteam1.refreshrator.entity.RecipeLike;
import com.hackathonteam1.refreshrator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLike, UUID> {
    List<RecipeLike> findAllByUser(User user);
}
