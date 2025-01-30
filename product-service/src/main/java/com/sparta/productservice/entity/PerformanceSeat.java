package com.sparta.productservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_seat")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PerformanceSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatCode; // 좌석 코드 (예: A1, B2)

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE; // 좌석 상태 (기본: AVAILABLE)

    public enum SeatStatus {
        AVAILABLE, SOLD, RESERVED, ON_HOLD
    }

    private LocalDateTime reservationTime; // 예약 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    @JsonBackReference // 직렬화 제외
    private Performance performance; // 공연 참조

}
