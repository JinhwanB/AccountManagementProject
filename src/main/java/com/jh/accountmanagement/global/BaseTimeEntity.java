package com.jh.accountmanagement.global;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter // 테스트용
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    @Column(updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime regDate;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime chgDate;
}
