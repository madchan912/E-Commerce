package com.sparta.productservice.controller;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.service.PerformanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.sparta.productservice.entity.PerformanceSeat.SeatStatus;

@RestController
@RequestMapping("/performances")
public class PerformanceController {

    private final PerformanceService performanceService;
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    /**
     * 공연 등록
     *
     * @param performance   공연 정보
     * @return 등록된 공연 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Performance createPerformance(@RequestBody Performance performance) {
        return performanceService.createPerformance(performance);
    }

    /**
     * 공연 정보 조회
     * 
     * @param id    공연 ID
     * @return  등록된 공연 정보
     */
    @GetMapping("/{id}")
    public Performance getPerformanceById(@PathVariable Long id) {
        return performanceService.getPerformanceById(id);
    }

    /**
     * 공연 좌석 등록
     * 
     * @param performanceId 공연 ID
     * @param seats 좌석 정보
     * @return  정상 등록 메시지
     */
    @PostMapping("/{performanceId}/seats")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addSeats(@PathVariable Long performanceId, @RequestBody List<PerformanceSeat> seats) {
        performanceService.addSeatsToPerformance(performanceId, seats);
        return ResponseEntity.status(HttpStatus.CREATED).body("Seats added successfully");
    }

    /**
     * 공연 좌석 조회
     *
     * @param performanceId 공연 ID
     * @return  해당 ID 좌석 전체
     */
    @GetMapping("/{performanceId}/seats")
    public List<PerformanceSeat> getSeatsByPerformance(@PathVariable Long performanceId) {
        return performanceService.getSeatsByPerformanceId(performanceId);
    }

    /**
     * 공연 좌석 상태 변경
     *
     * @param performanceId 공연 ID
     * @param seatCode  좌석위치
     * @param status    좌성 상태
     * @return  좌석 상태 변경 메시지
     */
    @PutMapping("/{performanceId}/seat/{seatCode}")
    public ResponseEntity<String> updateSeatStatus(@PathVariable Long performanceId,
                                                   @PathVariable String seatCode,
                                                   @RequestParam SeatStatus status) {
        performanceService.updateSeatStatus(performanceId, seatCode, status);
        return ResponseEntity.ok("Seat status updated successfully");
    }

    /**
     * 공연 삭제
     * 
     * @param performanceId 공연 ID
     * @return  공연 삭제 메시지
     */
    @DeleteMapping("/{performanceId}")
    public ResponseEntity<String> deletePerformance(@PathVariable Long performanceId) {
        performanceService.deletePerformance(performanceId);
        return ResponseEntity.ok("Performance and its seats deleted successfully");
    }

    /**
     * 공연 범위 검색
     * 
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */     
    @GetMapping
    public List<Performance> searchPerformances(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return performanceService.searchPerformances(name, startDate, endDate);
    }

    /**
     * 좌석 상태 통계 조회
     * 
     * @param performanceId 공연 ID
     * @return  좌석 상태 통계
     */
    @GetMapping("/{performanceId}/seats/status-summary")
    public Map<SeatStatus, Long> getSeatStatusSummary(@PathVariable Long performanceId) {
        return performanceService.getSeatStatusSummary(performanceId);
    }

}
