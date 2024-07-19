package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "ingredient", fetch = FetchType.LAZY)
    private List<FridgeItem> fridgeItems;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ingredient", fetch = FetchType.LAZY)
    private List<IngredientRecipe> ingredientRecipes;

}
