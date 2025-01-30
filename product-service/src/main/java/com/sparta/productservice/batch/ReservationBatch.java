package com.sparta.productservice.batch;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.entity.PerformanceSeat.SeatStatus;
import com.sparta.productservice.entity.Reservation.Status;
import com.sparta.productservice.repository.PerformanceRepository;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import com.sparta.productservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
public class ReservationBatch {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceSeatRepository performanceSeatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 10분마다 실행되며, 30분 이상 지난 RESERVED 좌석을 ON_HOLD로 변경
     */
    //@Scheduled(cron = "0 0/10 * * * ?") // 24시간 내내 10분 간격으로 실행
    @Transactional
    public void processExpiredReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<PerformanceSeat> reservedSeats = performanceSeatRepository.findSeatsByStatusAndTime(
                SeatStatus.RESERVED, threshold
        );

        reservedSeats.forEach(seat -> {
            // 좌석 상태를 ON_HOLD로 변경
            seat.setStatus(SeatStatus.ON_HOLD);
            performanceSeatRepository.save(seat);

            System.out.println("Marked seat ID " + seat.getId() + " as updated performanceSeat to ON_HOLD ");

            // 예약 상태를 FAILED로 변경
            reservationRepository.updateStatusBySeatId(seat.getId(), Status.CONFIRMED, Status.FAILED);

            System.out.println("Marked seat ID " + seat.getId() + " as updated reservation to FAILED.");
        });
    }

    /**
     * 10분마다 실행되며, "티켓 오픈 시간 + 1시간" 조건을 만족하는 좌석을 복구
     */
    //@Scheduled(cron = "0 0/10 * * * ?") // 24시간 내내 10분 간격으로 실행
    @Transactional
    public void restoreSeatsFromOnHold() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간 기준으로 "오픈 시간 <= 현재 < 공연 시작 시간" 공연 조회
        List<Performance> ongoingPerformances = performanceRepository.findOngoingPerformances(now);

        ongoingPerformances.forEach(performance -> {
            LocalDateTime openPlusOneHour = performance.getTicketOpeningTime().plusHours(1);

            // "오픈 시간 + 정수 * 1시간" 조건 확인
            if (now.isAfter(openPlusOneHour) && isIntegerHourElapsed(performance.getTicketOpeningTime(), now)) {
                restoreOnHoldSeats(performance);
            }
        });
    }

    private boolean isIntegerHourElapsed(LocalDateTime openingTime, LocalDateTime now) {
        long hoursElapsed = ChronoUnit.HOURS.between(openingTime, now);
        return hoursElapsed >= 1 && (hoursElapsed % 1 == 0); // 정수 시간 확인
    }

    private void restoreOnHoldSeats(Performance performance) {
        List<PerformanceSeat> onHoldSeats = performanceSeatRepository.findSeatsByStatusAndPerformance(
                SeatStatus.ON_HOLD, performance.getId()
        );

        onHoldSeats.forEach(seat -> {
            try {
                // PostgreSQL 상태 변경
                seat.setStatus(SeatStatus.AVAILABLE);
                performanceSeatRepository.save(seat);

                // Redis 상태 변경
                String redisKey = "performance:" + performance.getId() + ":seats";
                Map<String, Object> seatData = (Map<String, Object>) redisTemplate.opsForHash().get(redisKey, seat.getId().toString());
                if (seatData != null) {
                    seatData.put("status", "AVAILABLE");
                    redisTemplate.opsForHash().put(redisKey, seat.getId().toString(), seatData);
                }

                System.out.println("Restored seat ID " + seat.getId() + " for performance ID " + performance.getId());
            } catch (Exception ex) {
                // 간단한 예외 처리 (로그만 출력)
                System.err.println("Error restoring seat ID " + seat.getId() + ": " + ex.getMessage());
            }
        });
    }

}
