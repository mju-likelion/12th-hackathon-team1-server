package com.hackathonteam1.refreshrator.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.util.UUID;

@Entity(name = "image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "recipe")
public class Image extends BaseEntity{

    @Column(nullable = false)
    private String url; //aws s3의 url

}
