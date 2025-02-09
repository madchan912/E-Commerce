package com.sparta.productservice.service;

import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.entity.Reservation;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import com.sparta.productservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ReservationService(
            ReservationRepository reservationRepository,
            PerformanceSeatRepository performanceSeatRepository,
            @Qualifier("jsonRedisTemplate") RedisTemplate<String, Object> redisTemplate
    ) {
        this.reservationRepository = reservationRepository;
        this.performanceSeatRepository = performanceSeatRepository;
        this.redisTemplate = redisTemplate;
    }

    // 예약 생성
    @Transactional
    public Reservation createReservation(Long userId, Long performanceId, Long seatId) {
        String redisKey = "performance:" + performanceId + ":seats";
        Object rawData = redisTemplate.opsForHash().get(redisKey, String.valueOf(seatId));

        if (!(rawData instanceof Map<?, ?>)) {
            throw new IllegalStateException("Unexpected data format in Redis for seatId: " + seatId);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> seatData = new HashMap<>((Map<String, Object>) rawData);

        String seatStatus = (String) seatData.get("status");
        if ("RESERVED".equals(seatStatus)) {
            throw new IllegalStateException("The seat is already reserved. seatId: " + seatId);
        }

        seatData.put("status", "RESERVED");
        redisTemplate.opsForHash().put(redisKey, String.valueOf(seatId), seatData);

        System.out.println("RESERVED redis: " + seatId);

        PerformanceSeat seat = performanceSeatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found in PostgreSQL"));

        if (seat.getStatus() == PerformanceSeat.SeatStatus.RESERVED) {
            throw new IllegalStateException("The seat is already reserved. seatId: " + seatId);
        }

        seat.setStatus(PerformanceSeat.SeatStatus.RESERVED);
        seat.setReservationTime(LocalDateTime.now()); // 예약된 시간 저장
        performanceSeatRepository.save(seat);

        System.out.println("RESERVED seat. seatId: " + seatId);

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeat(seat);
        reservation.setPerformance(seat.getPerformance());
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(Reservation.Status.CONFIRMED);
        reservationRepository.save(reservation);

        System.out.println("ESERVED reservation. seatId: " + seatId);

        return reservation;
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

        // 예약 상태 변경
        reservation.setStatus(Reservation.Status.CANCELLED);
        reservationRepository.save(reservation);

        PerformanceSeat seat = reservation.getSeat();
        seat.setStatus(PerformanceSeat.SeatStatus.AVAILABLE);
        performanceSeatRepository.save(seat);

        // Redis 업데이트
        String redisKey = "performance:" + seat.getPerformance().getId() + ":seats";
        Object rawData = redisTemplate.opsForHash().get(redisKey, String.valueOf(seat.getId()));

        if (rawData instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> seatData = new HashMap<>((Map<String, Object>) rawData);
            seatData.put("status", "AVAILABLE");
            redisTemplate.opsForHash().put(redisKey, String.valueOf(seat.getId()), seatData);
        }
    }
}
