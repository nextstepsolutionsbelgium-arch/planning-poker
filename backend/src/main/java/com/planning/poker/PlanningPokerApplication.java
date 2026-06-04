package com.planning.poker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Planning Poker application.
 * Enables scheduling for automated session cleanup tasks.
 */
@SpringBootApplication
@EnableScheduling
public class PlanningPokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlanningPokerApplication.class, args);
    }
}
