package com.planning.poker.config;

import com.planning.poker.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task that runs every hour to remove poker sessions
 * that are older than 24 hours. Ensures session IDs are never reused
 * and the database stays clean.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupTask {

    private final SessionService sessionService;

    @Scheduled(fixedRate = 3600000)
    public void cleanup() {
        log.info("Cleaning up sessions older than 24h");
        sessionService.cleanupOldSessions();
    }
}
