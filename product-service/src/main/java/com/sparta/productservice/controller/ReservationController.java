package com.sparta.productservice.controller;

import com.sparta.productservice.entity.Reservation;
import com.sparta.productservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;

    /**
     * 예약 생성
     * 
     * @param userId    사용자 ID
     * @param performanceId 공연 ID
     * @param seatId    공연 좌석 ID
     * @return  예약 공연 정보
     */
    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestParam Long userId,
            @RequestParam Long performanceId,
            @RequestParam Long seatId) {
        Reservation reservation = reservationService.createReservation(userId, performanceId, seatId);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }


    /**
     * 사용자의 예약 목록 조회
     *
     * @param userId    사용자 ID
     * @return  해당 사용자 예약 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUser(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * 예약 취소
     * 
     * @param reservationId 예약 ID
     * @return  예약 취소 메시지
     */
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok("Reservation cancelled successfully.");
    }
}
