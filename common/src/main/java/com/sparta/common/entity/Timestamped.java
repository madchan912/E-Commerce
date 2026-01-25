package com.sparta.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 상속받은 엔티티들이 이 필드들을 컬럼으로 인식하도록 함
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화 (시간 자동 주입)
public abstract class Timestamped {

    @CreatedDate
    @Column(updatable = false) // 생성 후 변경되지 않음
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;
}
