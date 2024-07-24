package com.hackathonteam1.refreshrator.entity;

import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

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
    @Lob
    private String cookingStep;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecipeLike> recipeLikes;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IngredientRecipe> ingredientRecipes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    @Nullable
    private Image image;

    @Formula("(select count(rl.id) from recipe_like rl where rl.recipe_id = id)")
    private int likeCount;

    public void updateName(String name){
        this.name = name;
    }
    public void updateCookingStep(String cookingStep){
        this.cookingStep = cookingStep;
    }
    public void updateImage(Image image){
        if(this.image != null){
            throw new ConflictException(ErrorCode.RECIPE_IMAGE_CONFLICT);
        }
        this.image = image;
    }

    public Boolean isContainingImage(){
        return this.getImage()!=null;
    }

    public void deleteImage(){
        this.image = null;
    }
}
