package com.hackathonteam1.refreshrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass// 상속을 받는 Entity 클래스에게 매핑 정보만 제공
@Getter
@EntityListeners(AuditingEntityListener.class)// AuditingEntityListener는 엔티티의 생성 및 갱신 시간을 자동으로 설정하는 역할을 한다.
public class BaseEntity {
    //PK 생성
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @Column(updatable = false, unique = true, nullable = false)
    private UUID id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

