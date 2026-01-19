package com.sparta.productservice.repository;

import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.entity.PerformanceSeat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long> {
    Optional<PerformanceSeat> findByPerformanceIdAndSeatCode(Long performanceId, String seatCode);

    int countByPerformanceIdAndStatus(Long performanceId, PerformanceSeat.SeatStatus status);

    // 특정 상태와 시간 기준으로 좌석 조회
    @Query("SELECT ps FROM PerformanceSeat ps WHERE ps.status = :status AND ps.reservationTime <= :threshold")
    List<PerformanceSeat> findSeatsByStatusAndTime(SeatStatus status, LocalDateTime threshold);

    @Query("SELECT ps FROM PerformanceSeat ps WHERE ps.status = :status AND ps.performance.id = :performanceId")
    List<PerformanceSeat> findSeatsByStatusAndPerformance(@Param("status") PerformanceSeat.SeatStatus status, @Param("performanceId") Long performanceId);
}
