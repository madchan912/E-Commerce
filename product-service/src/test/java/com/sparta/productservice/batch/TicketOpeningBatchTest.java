package com.sparta.productservice.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TicketOpeningBatchTest {

    @Autowired
    private TicketOpeningBatch ticketOpeningBatch;

    @Test
    public void testCacheAllPerformances() {
        ticketOpeningBatch.cacheAllPerformances();
        System.out.println("Forced batch execution for all performances!");
    }
}
