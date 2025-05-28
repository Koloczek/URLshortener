package com.example.cleanup.scheduler;

import com.example.cleanup.service.CleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class CleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CleanupScheduler.class);

    private final CleanupService cleanupService;

    @Value("${cleanup.enabled:true}")
    private boolean cleanupEnabled;

    public CleanupScheduler(CleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Scheduled(cron = "${cleanup.schedule:0 0 1 * * ?}")
    public void runCleanupTask() {
        if (!cleanupEnabled) {
            logger.info("Cleanup is disabled; skipping execution.");
            return;
        }

        logger.info("Executing scheduled cleanup...");
        int deletedCount = cleanupService.cleanupOldUrls();
        logger.info("Cleanup finished; {} URLs deleted.", deletedCount);
    }
}
