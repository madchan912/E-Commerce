package com.sparta.productservice.repository;

import com.sparta.productservice.entity.Reservation;
import com.sparta.productservice.entity.Reservation.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserId(Long userId);

    // 특정 좌석 ID로 예약 상태 업데이트
    @Modifying
    @Query("UPDATE Reservation r SET r.status = :newStatus WHERE r.seat.id = :seatId AND r.status = :currentStatus")
    int updateStatusBySeatId(Long seatId, Status currentStatus, Status newStatus);

}
