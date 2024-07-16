package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity(name = "ingredient_recipe")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRecipe extends BaseEntity{

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Ingredient ingredient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Recipe recipe;

}
