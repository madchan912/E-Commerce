package com.sparta.productservice.service;

import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.entity.Reservation;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import com.sparta.productservice.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    public ReservationService(
            ReservationRepository reservationRepository,
            PerformanceSeatRepository performanceSeatRepository,
            @Qualifier("jsonRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            RedissonClient redissonClient
    ) {
        this.reservationRepository = reservationRepository;
        this.performanceSeatRepository = performanceSeatRepository;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    // 예약 생성 (Distributed Lock 적용)
    @Transactional
    public Reservation createReservation(Long userId, Long performanceId, Long seatId) {
        // Lock Key 생성 (좌석별로 잠금)
        String lockKet = "lock:seat:" + seatId;
        RLock lock = redissonClient.getLock(lockKet);

        try {
            // tryLock(waitTime, leaseTime, TimeUnit)
            // waitTime: 락 획득을 위해 기다리는 시간 (1초) - 너무 길면 대기열이 길어짐
            // leaseTime: 락을 잡고 잇는 최대 시간 (3초) - 데드락 방지
            boolean isLocked = lock.tryLock(1, 3, TimeUnit.SECONDS);

            if (!isLocked) {
                log.info("Lock acquisition failed. Seat ID: " + seatId + ", User ID: " + userId);
                throw new RuntimeException("Failed to acquire lock (Traffic congestion).");
            }

            // --- Critical Section (락 획득 성공 시 실행) ---

            // 1. Redis 데이터 검증
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

            // 2. Redis 상태 업데이트
            seatData.put("status", "RESERVED");
            redisTemplate.opsForHash().put(redisKey, String.valueOf(seatId), seatData);

            log.info("Redis status updated to RESERVED. seatId: {}", seatId);

            // 3. DB 상태 업데이트
            PerformanceSeat seat = performanceSeatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found in PostgreSQL"));

            if (seat.getStatus() == PerformanceSeat.SeatStatus.RESERVED) {
                throw new IllegalStateException("The seat is already reserved. seatId: " + seatId);
            }

            seat.setStatus(PerformanceSeat.SeatStatus.RESERVED);
            seat.setReservationTime(LocalDateTime.now()); // 예약된 시간 저장
            performanceSeatRepository.save(seat);

            log.info("DB status updated. seatId: {}", seatId);

            // 4. 예약 정보 생성
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setSeat(seat);
            reservation.setPerformance(seat.getPerformance());
            reservation.setReservationTime(LocalDateTime.now());
            reservation.setStatus(Reservation.Status.CONFIRMED);
            reservationRepository.save(reservation);

            log.info("Reservation Confirmed. User: {}, Seat: {}", userId, seatId);

            return reservation;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition interrupted", e);
        } finally {
            // 락 해제 (현재 스레드가 락을 가지고 있을 때만)
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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

        log.info("Reservation Cancelled. ID: {}", reservationId);
    }
}
