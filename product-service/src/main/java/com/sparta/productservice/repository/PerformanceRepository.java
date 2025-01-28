package com.sparta.productservice.repository;

import com.sparta.productservice.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    // 티켓 오픈 시간이 특정 시간 범위에 있는 공연 조회
    @Query("SELECT p FROM Performance p WHERE p.ticketOpeningTime BETWEEN :start AND :end")
    List<Performance> findByTicketOpeningTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Performance p WHERE p.ticketOpeningTime <= :now AND p.date > :now")
    List<Performance> findOngoingPerformances(@Param("now") LocalDateTime now);

}
