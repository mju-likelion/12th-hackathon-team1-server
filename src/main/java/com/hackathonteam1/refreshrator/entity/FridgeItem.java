package com.hackathonteam1.refreshrator.entity;

import com.hackathonteam1.refreshrator.exception.BadRequestException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "fridge_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    //저장방법 결정 메서드
    public static Storage of(String storage){
        return switch (storage){
            case "상온"-> FridgeItem.Storage.STORE_AT_ROOM_TEMPERATURE;
            case "냉동"-> FridgeItem.Storage.FROZEN;
            case "냉장" -> FridgeItem.Storage.REFRIGERATED;
            default -> throw new BadRequestException(ErrorCode.STORAGE_ERROR);
        };
    }

    public boolean isExpired(){
        return LocalDate.now().isAfter(this.expiredDate);
    }
}