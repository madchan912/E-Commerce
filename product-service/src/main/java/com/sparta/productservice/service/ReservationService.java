package com.sparta.productservice.service;

import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.entity.Reservation;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import com.sparta.productservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 예약 생성
    @Transactional
    public Reservation createReservation(Long userId, Long performanceId, Long seatId) {
        String redisKey = "performance:" + performanceId + ":seats";
        Map<String, Object> seatData = (Map<String, Object>) redisTemplate.opsForHash().get(redisKey, String.valueOf(seatId));

        if (seatData == null) {
            throw new IllegalStateException("Seat data not found.");
        }

        String seatStatus = (String) seatData.get("status");
        if ("RESERVED".equals(seatStatus)) {
            throw new IllegalStateException("The seat is already reserved.");
        }

        seatData = new HashMap<>(seatData);

        seatData.put("status", "RESERVED");
        redisTemplate.opsForHash().put(redisKey, String.valueOf(seatId), seatData);

        System.out.println("RESERVED redis");

        PerformanceSeat seat = performanceSeatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found in PostgreSQL"));

        seat.setStatus(PerformanceSeat.SeatStatus.RESERVED);
        performanceSeatRepository.save(seat);

        System.out.println("RESERVED seat");

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeat(seat);
        reservation.setPerformance(seat.getPerformance());
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(Reservation.Status.CONFIRMED);
        reservationRepository.save(reservation);

        System.out.println("RESERVED reservation");

        return reservationRepository.save(reservation);
    }

    // 특정 사용자 예약 조회
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findAllByUserId(userId);
    }

    // 예약 취소
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == Reservation.Status.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
        }

        reservation.setStatus(Reservation.Status.CANCELLED);
        reservationRepository.save(reservation);

        PerformanceSeat seat = reservation.getSeat();
        seat.setStatus(PerformanceSeat.SeatStatus.AVAILABLE);
        performanceSeatRepository.save(seat);

        String redisKey = "performance:" + seat.getPerformance().getId() + ":seats";
        Map<String, Object> seatData = (Map<String, Object>) redisTemplate.opsForHash().get(redisKey, String.valueOf(seat.getId()));

        if (seatData != null) {
            seatData.put("status", "AVAILABLE");
            redisTemplate.opsForHash().put(redisKey, String.valueOf(seat.getId()), seatData);
        }
    }
}
