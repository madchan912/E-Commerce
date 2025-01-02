package com.sparta.productservice.repository;

import com.sparta.productservice.entity.PerformanceSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long> {
    Optional<PerformanceSeat> findByPerformanceIdAndSeatCode(Long performanceId, String seatCode);
}
