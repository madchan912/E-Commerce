package com.sparta.productservice.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class PerformanceCleanupScheduler {

    private final JobLauncher jobLauncher;
    private final Job performanceCleanupJob;

    // 매일 자정(00:00:00)에 실행
    // cron 표현식: 초 분 시 일 월 요일
//    @Scheduled(cron = "0 0 0 * * *")
    public void runCleanupJob() {
        log.info("=== [Scheduler] 공연 상태 변경 배치 시작 ===");

        try {
            // JobParameters: 배치를 실행할 때마다 유니크한 파라미터를 줘야 중복 실행으로 인식 안 함
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("executedTime", LocalDateTime.now().toString())
                    .toJobParameters();

            jobLauncher.run(performanceCleanupJob, jobParameters);

        } catch (Exception e) {
            log.error("=== [Scheduler] 배치 실행 중 오류 발생: ", e);
        }

        log.info("=== [Scheduler] 공연 상태 변경 배치 종료 ===");
    }
}
