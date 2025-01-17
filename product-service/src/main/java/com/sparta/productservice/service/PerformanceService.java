package com.sparta.productservice.service;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.repository.PerformanceRepository;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 공연에 좌석 등록
     * @param performanceId 공연 ID
     * @param zone 구역명 (예: A, B 등)
     * @param seatCount 좌석 수
     * @return 생성된 좌석 목록
     */
    public List<PerformanceSeat> createSeatsForPerformance(Long performanceId, String zone, int seatCount) {
        Performance performance = getPerformanceById(performanceId);

        List<PerformanceSeat> seats = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            PerformanceSeat seat = new PerformanceSeat();
            seat.setSeatCode(zone + i); // 예: A1, A2...
            seat.setPerformance(performance);
            seats.add(seat);
        }

        // 좌석을 데이터베이스에 저장
        return performanceSeatRepository.saveAll(seats);
    }

    // 예약 가능 좌석 조회
    public int getAvailableSeatCount(Long performanceId) {
        return performanceSeatRepository.countByPerformanceIdAndStatus(
                performanceId, PerformanceSeat.SeatStatus.AVAILABLE);
    }

}