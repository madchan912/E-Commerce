package com.sparta.productservice.service;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.repository.PerformanceRepository;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sparta.productservice.entity.PerformanceSeat.SeatStatus;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

    public PerformanceService(PerformanceRepository performanceRepository, PerformanceSeatRepository performanceSeatRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceSeatRepository = performanceSeatRepository;
    }

    // 공연 생성
    public Performance createPerformance(Performance performance) {
        LocalDateTime adjustedTime = performance.getDate().withSecond(0).withNano(0);
        performance.setDate(adjustedTime);
        return performanceRepository.save(performance);
    }

    // 공연 정보 조회
    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));
    }

    // 공연 좌석 등록
    @Transactional
    public void addSeatsToPerformance(Long performanceId, List<PerformanceSeat> seats) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));

        for (PerformanceSeat seat : seats) {
            seat.setPerformance(performance); // 공연과 좌석 연결
        }

        performanceSeatRepository.saveAll(seats);
    }

    // 공연 좌석 조회
    public List<PerformanceSeat> getSeatsByPerformanceId(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));
        return performance.getSeats(); // 공연의 좌석 목록 반환
    }

    // 공연 좌석 상태 변경
    public void updateSeatStatus(Long performanceId, String seatCode, SeatStatus status) {
        PerformanceSeat seat = performanceSeatRepository.findByPerformanceIdAndSeatCode(performanceId, seatCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        seat.setStatus(status); // 좌석 상태 업데이트
        performanceSeatRepository.save(seat);
    }

    // 공연 삭제
    @Transactional
    public void deletePerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));

        performanceRepository.delete(performance); // 공연과 관련 좌석 삭제 (Cascade)
    }

    // 공연 검색
    public List<Performance> searchPerformances(String name, LocalDate startDate, LocalDate endDate) {
        List<Performance> results;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59);
            if (name != null) {
                results = performanceRepository.findByNameContainingAndDateBetweenWithoutSeats(name, start, end);
            } else {
                results = performanceRepository.findByDateBetweenWithoutSeats(start, end);
            }
        } else if (name != null) {
            results = performanceRepository.findByNameContaining(name);
        } else {
            results = Collections.emptyList(); // 빈 리스트 반환
        }

        // 검색 결과가 없을 경우 예외 발생
        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No performances found for the given criteria");
        }

        return results;
    }



    // 좌석 상태 통계 조회
    public Map<SeatStatus, Long> getSeatStatusSummary(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));

        return performance.getSeats().stream()
                .collect(Collectors.groupingBy(PerformanceSeat::getStatus, Collectors.counting()));
    }

}
