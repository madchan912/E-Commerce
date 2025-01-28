package com.sparta.productservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 예약자 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    @JsonManagedReference  // 직렬화 제외
    private PerformanceSeat seat; // 공연 좌석과 다대일 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false) // 공연 ID 추가
    @JsonIgnore
    private Performance performance; // 공연 정보

    private LocalDateTime reservationTime; // 예약 시간

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // 예약 상태 기본값

    public enum Status {
        PENDING, CONFIRMED, CANCELLED, FAILED, COMPLETED
    }
}
