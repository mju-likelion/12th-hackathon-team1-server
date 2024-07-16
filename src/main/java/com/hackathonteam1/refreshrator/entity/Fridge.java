package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

//fridge와 user는 단방향.
@Entity(name = "fridge")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fridge extends BaseEntity{

    @OneToMany(mappedBy = "fridge", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FridgeItem> fridgeItem;

}
