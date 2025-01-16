package com.sparta.productservice.controller;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * 공연 등록
     *
     * @param performance 공연 정보
     * @return 등록된 공연 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Performance createPerformance(@RequestBody Performance performance) {
        return performanceService.createPerformance(performance);
    }

    /**
     * 모든 공연 조회
     *
     * @return 공연 리스트
     */
    @GetMapping
    public List<Performance> getAllPerformances() {
        return performanceService.getAllPerformances();
    }

    /**
     * 특정 공연 조회
     *
     * @param id 공연 ID
     * @return 공연 정보
     */
    @GetMapping("/{id}")
    public Performance getPerformanceById(@PathVariable Long id) {
        return performanceService.getPerformanceById(id);
    }

    /**
     * 좌석 등록
     *
     * @param performanceId 공연 ID
     * @param zone 구역명 (예: A, B 등)
     * @param seatCount 좌석 수
     * @return 생성된 좌석 목록
     */
    @PostMapping("/{performanceId}/seats")
    public List<PerformanceSeat> createSeatsForPerformance(@PathVariable Long performanceId,
                                                           @RequestParam String zone,
                                                           @RequestParam int seatCount) {
        return performanceService.createSeatsForPerformance(performanceId, zone, seatCount);
    }
}
