package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "fridge_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FridgeItem extends BaseEntity{

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id", nullable = false)
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private LocalDate expiredDate;

    @Column
    private int quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Storage storage;

    @Column
    private String memo;

    public enum Storage{
        STORE_AT_ROOM_TEMPERATURE, REFRIGERATED, FROZEN;
    }
}