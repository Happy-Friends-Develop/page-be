package com.example.hello_friends.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailySettlementJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // Job (하나의 큰 작업) 정의
    @Bean
    public Job dailySettlementJob() {
        log.info("'dailySettlementJob' Job Bean 등록");

        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(dailySettlementStep1())
                .build();
    }

    // Step (작업의 한 단계)을 정의
    @Bean
    public Step dailySettlementStep1() {
        log.info("'dailySettlementStep1' Step Bean 등록");

        return new StepBuilder("dailySettlementStep1", jobRepository)
                // 이 Step은 'simpleTasklet'이라는 간단한 일을 수행합니다.
                .tasklet(simpleTasklet(), transactionManager)
                .build();
    }

    // Tasklet (Step이 수행하는 가장 간단한 일의 단위)을 정의
    @Bean
    public Tasklet simpleTasklet() {
        log.info(">>>>> 'simpleTasklet' Tasklet Bean 등록");

        return (contribution, chunkContext) -> {
            // Tasklet이 실행되는 시점에 이 코드가 동작합니다.
            log.info(">>>>>>>>>> Hello, Spring Batch! (일일 정산 시작)");
            log.info(">>>>>>>>>> (정산 로직 수행중...)");
            log.info(">>>>>>>>>> Hello, Spring Batch! (일일 정산 완료)");

            // 작업을 완료하고 종료합
            return RepeatStatus.FINISHED;
        };
    }
}
