package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeLike extends BaseEntity{

    @ManyToOne(optional = true)
    private User user;

}
