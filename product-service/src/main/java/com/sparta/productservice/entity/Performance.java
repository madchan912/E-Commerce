package com.sparta.productservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 공연명

    private LocalDateTime date; // 공연 날짜

    private String location; // 공연 장소
    
    private LocalDateTime ticketOpeningTime; // 티켓 오픈 일정

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference //직렬화 허용
    private List<PerformanceSeat> seats; // 좌석 목록
}
