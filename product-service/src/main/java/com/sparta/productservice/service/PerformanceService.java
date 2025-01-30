package com.sparta.productservice.service;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.repository.PerformanceRepository;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 공연 등록
    public Performance createPerformance(Performance performance) {
        performance.setDate(performance.getDate().withSecond(0).withNano(0));
        return performanceRepository.save(performance);
    }

    // 모든 공연 조회
    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    // 특정 공연 조회
    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));
    }

    //공연에 좌석 등록
    public List<PerformanceSeat> createSeatsForPerformance(Long performanceId, String zone, int seatCount) {
        Performance performance = getPerformanceById(performanceId);

        List<PerformanceSeat> seats = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            PerformanceSeat seat = new PerformanceSeat();
            seat.setSeatCode(zone + i); // 예: A1, A2...
            seat.setPerformance(performance);
            seats.add(seat);
        }

        return performanceSeatRepository.saveAll(seats);
    }

    // 예약 가능 좌석 조회
    public int getAvailableSeatCount(Long performanceId) {
        return performanceSeatRepository.countByPerformanceIdAndStatus(
                performanceId, PerformanceSeat.SeatStatus.AVAILABLE);
    }

    // 특정 공연 전체 좌석 현황 조회
    public ResponseEntity<?> getPerformanceSeats(Long performanceId) {
        if (Math.random() < 0.2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Please try again.");
        }

        String redisKey = "performance:" + performanceId + ":seats";
        Map<Object, Object> seatData = redisTemplate.opsForHash().entries(redisKey);

        if (seatData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No seat information available for this performance.");
        }

        return ResponseEntity.ok(seatData);
    }
}