package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.repository.cdi.Eager;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

}
