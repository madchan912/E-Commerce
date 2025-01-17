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
import java.util.Map;

@Component
public class TicketOpeningBatch {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 10분마다 실행되며, 티켓 오픈 시간이 다가온 공연의 좌석 데이터를 Redis에 캐싱
     */
    //@Scheduled(fixedRate = 600000) // 10분마다 실행
    public void cacheUpcomingPerformances() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcomingWindow = now.plusMinutes(15); // 15분 이내 티켓 오픈 공연

        // 티켓 오픈 시간이 15분 이내로 다가온 공연 조회
        List<Performance> performances = performanceRepository.findByTicketOpeningTimeBetween(now, upcomingWindow);

        performances.forEach(performance -> {
            String redisKey = "performance:" + performance.getId() + ":seats";

            // Redis Hash에 각 좌석 데이터를 저장
            performance.getSeats().forEach(seat -> {
                redisTemplate.opsForHash().put(redisKey, String.valueOf(seat.getId()), Map.of(
                        "id", seat.getId(),
                        "seatCode", seat.getSeatCode(),
                        "status", seat.getStatus().name() // Enum은 String으로 변환
                ));
            });

            // Redis Key의 TTL 설정 (공연 종료 시간 기준)
            redisTemplate.expireAt(redisKey, java.sql.Timestamp.valueOf(performance.getDate()));
            System.out.println("Cached seats for performance: " + performance.getName());
        });
    }


    /**
     * 모든 공연의 좌석 데이터를 Redis에 캐싱(테스트용)
     */
    @Transactional
    public void cacheAllPerformances() {
        List<Performance> performances = performanceRepository.findAll();

        performances.forEach(performance -> {
            // Lazy 로딩된 필드 강제 초기화
            performance.getSeats().size();

            // Redis Key 생성
            String redisKey = "performance:" + performance.getId() + ":seats";

            // Redis Hash에 좌석 데이터 저장
            performance.getSeats().forEach(seat -> {
                redisTemplate.opsForHash().put(redisKey, String.valueOf(seat.getId()), Map.of(
                        "id", seat.getId(),
                        "seatCode", seat.getSeatCode(),
                        "status", seat.getStatus().name() // Enum을 String으로 변환
                ));
            });

            // Redis Key의 TTL 설정 (공연 종료 시간 기준)
            redisTemplate.expireAt(redisKey, java.sql.Timestamp.valueOf(performance.getDate()));
            System.out.println("Cached seats for date: " + performance.getDate());
            System.out.println("Cached seats for performance: " + performance.getName());
        });
    }


}
