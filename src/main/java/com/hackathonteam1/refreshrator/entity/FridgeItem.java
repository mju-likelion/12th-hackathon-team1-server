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
    private Fridge fridge;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Ingredient ingredient;

    @Column()
    private LocalDate expiredDate;

    @Column
    private int quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Storage storage;

    @Column
    private String memo;

    @Column
    private boolean expired;

    public enum Storage{
        STORE_AT_ROOM_TEMPERATURE, REFRIGERATED, FROZEN;
    }
}
