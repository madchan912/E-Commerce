package com.sparta.productservice.batch;

import com.sparta.productservice.entity.Performance;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PerformanceCleanupJobConfig {

    private final EntityManagerFactory entityManagerFactory;

    // 1. Job 생성 (배치 작업의 전체 단위 / "공연 마감 처리" 작업 지시서)
    @Bean
    public Job performanceCleanupJob(JobRepository jobRepository, Step performanceCleanupStep) {
        return new JobBuilder("performanceCleanupJob", jobRepository)
                .start(performanceCleanupStep) // 해당 Step 실행
                .build();
    }

    // 2. Step 생성 (실질적인 작업 수행 / 읽기 -> 가공 -> 쓰기)
    @Bean
    public Step performanceCleanupStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("performanceCleanupStep", jobRepository)
                .<Performance, Performance>chunk(10, transactionManager) // 10개씩 끊어서 처리 (OOM 방지 및 트랜잭션 단위)
                .reader(performanceReader())      // [READ]
                .processor(performanceProcessor()) // [PROCESS]
                .writer(performanceWriter())      // [WRITE]
                .build();
    }

    // [Reader] DB에서 데이터 조회 (Paging 기법으로 10개씩 끊어 읽기)
    @Bean
    public JpaPagingItemReader<Performance> performanceReader() {
        return new JpaPagingItemReaderBuilder<Performance>()
                .name("performanceReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10) // Chunk 사이즈와 동일하게 설정
                .queryString("SELECT p FROM Performance p WHERE p.endDate < :now AND p.status = 'AVAILABLE'")
                .parameterValues(Map.of("now", LocalDateTime.now()))
                .build();
    }

    // [Processor] 조회한 데이터 가공 (상태 변경 로직 수행)
    @Bean
    public ItemProcessor<Performance, Performance> performanceProcessor() {
        return performance -> {
            log.info("Closing performance: {}", performance.getName());
            performance.setStatus(Performance.Status.CLOSED); // 상태값 변경 (AVAILABLE -> CLOSED)
            return performance;
        };
    }

    // [Writer] 변경된 데이터 DB 반영 (JPA Dirty Checking으로 자동 Update 실행)
    @Bean
    public JpaItemWriter<Performance> performanceWriter() {
        return new JpaItemWriterBuilder<Performance>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
