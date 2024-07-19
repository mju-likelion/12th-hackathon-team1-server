package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "recipe")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String cookingStep;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecipeLike> recipeLikes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

}
