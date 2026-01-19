package com.sparta.productservice.service;

import com.sparta.productservice.batch.TicketOpeningBatch;
import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.repository.PerformanceRepository;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import com.sparta.productservice.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TicketOpeningBatch ticketOpeningBatch;

    // 초기화를 위한 레포지토리 주입
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PerformanceSeatRepository performanceSeatRepository;

    @BeforeEach
    void setUp() {
        System.out.println("테스트 환경 초기화 시작");
        // 1. 기존 예약 내역 모두 삭제
        reservationRepository.deleteAll();

        // 2. 1번 좌석을 강제로 'AVAILABLE' 상태로 DB 원복
        PerformanceSeat seat = performanceSeatRepository.findById(1L).orElseThrow();
        seat.setStatus(PerformanceSeat.SeatStatus.AVAILABLE);
        performanceSeatRepository.save(seat);

        // 3. 초기화된 DB 상태를 Reids에 통기화
        ticketOpeningBatch.cachePerformance(1L);

        System.out.println("Redis 캐싱 완료");
    }

    @Test
    @DisplayName("락 없이 동시성 테스트: 100명이 동시에 1번 좌석을 예매하면 망한다")
    void testConcurrencyWithoutLock() throws InterruptedException {
        // Given: 100명의 유저가 동시에 접속
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads); // 100명이 다 준비될 때까지 대기

        AtomicInteger successCount = new AtomicInteger(); // 성공한 횟수 카운트
        AtomicInteger failCount = new AtomicInteger();    // 실패한 횟수 카운트

        // When: 동시에 예약 요청 (공연ID: 1, 좌석ID: 1)
        // 주의: init.sql로 데이터를 넣었으므로 좌석 ID 1번이 존재한다고 가정
        for (int i = 1; i <= numberOfThreads; i++) {
            Long userId = (long) i; // 유저 ID 1~100
            executorService.submit(() -> {
                try {
                    // 예약 시도 (유저ID, 공연ID=1, 좌석ID=1)
                    reservationService.createReservation(userId, 1L, 1L);
                    successCount.getAndIncrement(); // 예외 안 나면 성공
                    System.out.println("예약 성공 User ID: " + userId);
                } catch (Exception e) {
                    failCount.getAndIncrement(); // 예외 나면 실패
                    System.out.println("예약 실패: " + e.getMessage());
                } finally {
                    latch.countDown(); // 카운트 감소
                }
            });
        }

        latch.await(); // 100명이 다 끝날 때까지 대기

        latch.await();

        System.out.println("===============================================");
        System.out.println("Total Requests: " + numberOfThreads);
        System.out.println("Success Count : " + successCount.get());
        System.out.println("Failed Count  : " + failCount.get());
        System.out.println("===============================================");

        // [검증]
        // Redisson 분산 락 적용 후에는 무조건 1명만 성공해야 한다.
        assertEquals(1, successCount.get(), "Concurrency control failed. Multiple reservations occurred.");
    }
}