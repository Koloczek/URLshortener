package com.example.cleanup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Instant;
import java.time.ZoneId;

@SpringBootApplication
@EnableScheduling
public class CleanupApplication {
    private static final Logger logger = LoggerFactory.getLogger(CleanupApplication.class);

    @PostConstruct
    public void logDefaultTimeZone() {
        logger.info("JVM default TimeZone: {}  |  Instant.now(): {}",
                ZoneId.systemDefault(), Instant.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(CleanupApplication.class, args);
    }
}
