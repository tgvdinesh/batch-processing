package com.example.batchprocessing.configuration;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@RequiredArgsConstructor
@Configuration
public class MultiStepConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiStepConfiguration.class.getName());

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiStepJob(Step readFromDatabaseStep, Step doANetworkCall, Step updateDatabase) {
        return this.jobBuilderFactory.get("multiStepJob")
                .incrementer(new RunIdIncrementer())
                .start(readFromDatabaseStep)
                .next(doANetworkCall)
                .next(updateDatabase)
                .build();
    }

    @Bean
    public Step readFromDatabaseStep() {
        return this.stepBuilderFactory.get("readFromDatabaseStep")
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(2000);
                    LOGGER.info("Read from Database at " + Instant.now()); // Blocking
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step doANetworkCall() {
        return this.stepBuilderFactory.get("doANetworkCall")
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(5000);
                    LOGGER.info("Made a network call at " + Instant.now()); // Blocking
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step updateDatabase() {
        return this.stepBuilderFactory.get("updateDatabase")
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(3000);
                    LOGGER.info("Updated Database at " + Instant.now()); // Blocking
                    return RepeatStatus.FINISHED;
                }).build();
    }
}

