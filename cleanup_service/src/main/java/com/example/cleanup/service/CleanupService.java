package com.example.cleanup.service;

import com.example.cleanup.model.ShortUrlEntity;
import com.example.cleanup.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    private final ShortUrlRepository repository;

    @Value("${cleanup.max-age-minutes:3}")
    private long maxAgeMinutes;

    @Value("${cleanup.inactive-minutes:3}")
    private long inactiveMinutes;

    @Value("${cleanup.strategy:CREATION_TIME}")
    private CleanupStrategy strategy;

    public enum CleanupStrategy {
        CREATION_TIME,
        LAST_ACCESS_TIME,
        EXPIRATION_TIME
    }

    public CleanupService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    public int cleanupOldUrls() {
        Instant now = Instant.now();
        List<ShortUrlEntity> candidates;
        Instant cutoff;

        switch (strategy) {
            case CREATION_TIME:
                cutoff = now.minus(Duration.ofMinutes(maxAgeMinutes));
                long cutoffMs     = cutoff.toEpochMilli();
                long cutoffSec    = cutoff.getEpochSecond();
                logger.info("CREATION_TIME: cutoff at {} (ms: {}, sec: {})", cutoff, cutoffMs, cutoffSec);

                // wybierz jedną z poniższych linii zgodnie z tym, co masz w Cassandra:
                // jeśli w Cassandra trzymasz MILLISEKUNDY --> użyj ms
                candidates = repository.findByCreationTimeBefore(cutoffMs);
                // jeśli w Cassandra trzymasz SEKUNDY --> zakomentuj wyżej i odkomentuj poniżej
                // candidates = repository.findByCreationTimeBefore(cutoffSec);
                break;

            case LAST_ACCESS_TIME:
                cutoff = now.minus(Duration.ofMinutes(inactiveMinutes));
                logger.info("LAST_ACCESS_TIME: cutoff at {}", cutoff);
                candidates = repository.findByLastAccessTimeBefore(cutoff.toEpochMilli());
                break;

            case EXPIRATION_TIME:
                cutoff = now;
                logger.info("EXPIRATION_TIME: cutoff at {}", cutoff);
                candidates = repository.findByExpirationTimeBefore(cutoff.toEpochMilli());
                break;

            default:
                logger.warn("Unknown cleanup strategy: {}", strategy);
                return 0;
        }

        int count = candidates.size();
        if (count > 0) {
            logger.info("Found {} URLs to delete: {}", count, candidates);
            repository.deleteAll(candidates);
        } else {
            logger.info("No expired URLs found.");
        }

        return count;
    }
}
