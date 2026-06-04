package com.planning.poker.repository;

import com.planning.poker.model.PokerSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing {@link PokerSession} entities.
 * Provides lookup by creation time for scheduled cleanup of expired sessions.
 */
public interface SessionRepository extends JpaRepository<PokerSession, String> {
    List<PokerSession> findByCreatedAtBefore(LocalDateTime cutoff);
}
