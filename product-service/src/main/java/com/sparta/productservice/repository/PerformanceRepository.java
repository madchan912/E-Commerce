package com.sparta.productservice.repository;

import com.sparta.productservice.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByNameContaining(String name);
    @Query("SELECT p FROM Performance p WHERE p.date BETWEEN :start AND :end")
    List<Performance> findByDateBetweenWithoutSeats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Performance p WHERE p.date BETWEEN :start AND :end AND p.name LIKE %:name%")
    List<Performance> findByNameContainingAndDateBetweenWithoutSeats(String name, LocalDateTime start, LocalDateTime end);

    // 티켓 오픈 시간이 특정 시간 범위에 있는 공연 조회
    @Query("SELECT p FROM Performance p WHERE p.ticketOpeningTime BETWEEN :start AND :end")
    List<Performance> findByTicketOpeningTimeBetween(LocalDateTime start, LocalDateTime end);
}
