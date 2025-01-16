package com.sparta.productservice.batch;

import com.sparta.productservice.entity.Performance;
import com.sparta.productservice.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TicketOpeningBatch {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    @Qualifier("seatRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 10분마다 실행되며, 티켓 오픈 시간이 다가온 공연의 좌석 데이터를 Redis에 캐싱
     */
    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void cacheUpcomingPerformances() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcomingWindow = now.plusMinutes(15); // 15분 이내 티켓 오픈 공연

        // 티켓 오픈 시간이 15분 이내로 다가온 공연 조회
        List<Performance> performances = performanceRepository.findByTicketOpeningTimeBetween(now, upcomingWindow);

        performances.forEach(performance -> {
            String redisKey = "performance:" + performance.getId() + ":seats";
            redisTemplate.opsForValue().set(redisKey, performance.getSeats()); // 좌석 데이터를 Redis에 캐싱
            redisTemplate.expireAt(redisKey, java.sql.Timestamp.valueOf(performance.getDate())); // 공연 시간까지 데이터 유지
            System.out.println("Cached seats for performance: " + performance.getName());
        });
    }

    /**
     * 모든 공연의 좌석 데이터를 Redis에 캐싱
     */
    @Transactional
    public void cacheAllPerformances() {
        List<Performance> performances = performanceRepository.findAll();

        performances.forEach(performance -> {
            // Lazy 로딩된 필드 강제 초기화
            performance.getSeats().size();

            // Redis에 데이터 저장
            String redisKey = "performance:" + performance.getId() + ":seats";
            redisTemplate.opsForValue().set(redisKey, performance.getSeats());
            redisTemplate.expireAt(redisKey, java.sql.Timestamp.valueOf(performance.getDate()));

            System.out.println("Cached seats for performance: " + performance.getName());
        });
    }

}
