package com.sparta.productservice.service;

import com.sparta.productservice.entity.PerformanceSeat;
import com.sparta.productservice.entity.Reservation;
import com.sparta.productservice.repository.PerformanceSeatRepository;
import com.sparta.productservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

    // 예약 생성
    public Reservation createReservation(Long userId, Long seatId) {
        PerformanceSeat seat = performanceSeatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found"));

        // 동시 예약 충돌 처리
        if (seat.getStatus() != PerformanceSeat.SeatStatus.AVAILABLE) {
            return handleFailedReservation(userId, seat, "Seat is already reserved or sold.");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeat(seat);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(Reservation.Status.PENDING);

        // 좌석 상태 변경
        seat.setStatus(PerformanceSeat.SeatStatus.RESERVED);
        performanceSeatRepository.save(seat);

        return reservationRepository.save(reservation);
    }

    // 예약 실패 처리
    private Reservation handleFailedReservation(Long userId, PerformanceSeat seat, String reason) {
        Reservation failedReservation = new Reservation();
        failedReservation.setUserId(userId);
        failedReservation.setSeat(seat);
        failedReservation.setReservationTime(LocalDateTime.now());
        failedReservation.setStatus(Reservation.Status.FAILED);

        System.out.println("Reservation failed: " + reason);
        return reservationRepository.save(failedReservation);
    }

    // 특정 사용자 예약 조회
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findAllByUserId(userId);
    }

    // 예약 취소
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == Reservation.Status.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
        }

        reservation.setStatus(Reservation.Status.CANCELLED);
        reservationRepository.save(reservation);

        // 좌석 상태 복구
        PerformanceSeat seat = reservation.getSeat();
        seat.setStatus(PerformanceSeat.SeatStatus.AVAILABLE);
        performanceSeatRepository.save(seat);
    }
}
