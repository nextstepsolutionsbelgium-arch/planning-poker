package com.planning.poker.repository;

import com.planning.poker.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing {@link Participant} entities.
 * Supports lookup by session ID and participant name to enforce unique names per session.
 */
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findBySessionIdAndName(String sessionId, String name);
}
