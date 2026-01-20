package com.sparta.productservice.batch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TicketOpeningBatchTest {

    @Autowired
    private TicketOpeningBatch ticketOpeningBatch;

    @Test
    public void testCacheAllPerformances() {
        ticketOpeningBatch.cachePerformance(1L);
        log.info("Forced batch execution for all performances!");
    }
}
