package com.sparta.productservice.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReservationBatchTest {
    @Autowired
    private ReservationBatch reservationBatch;

    @Test
    public void testProcessExpiredReservations() {
        // ON_HOLD로 변경하는 배치
        reservationBatch.processExpiredReservations();
        // ON_HOLD 좌석을 AVAILABLE로 변경하는 배치
        reservationBatch.restoreSeatsFromOnHold();
        System.out.println("ReservationBatch executed successfully.");
    }
}
