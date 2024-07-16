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
public class Image {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String fileName; //id(uuid)+기존 파일이름

    @OneToOne(fetch = FetchType.LAZY, optional = false, mappedBy = "image_id")
    private Recipe recipe;

}
