package com.planning.poker.service;

import com.planning.poker.model.Participant;
import com.planning.poker.model.PokerSession;
import com.planning.poker.repository.ParticipantRepository;
import com.planning.poker.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for planning poker session management.
 * Handles session creation, participant joining, vote submission,
 * vote reveal, round reset, and cleanup of expired sessions.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Creates a new poker session with a unique UUID.
     *
     * @return the newly created session
     */
    public PokerSession createSession() {
        return sessionRepository.save(new PokerSession());
    }

    /**
     * Retrieves a session by ID.
     *
     * @param id the session UUID
     * @return the session
     * @throws ResponseStatusException 404 if session not found
     */
    public PokerSession getSession(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    /**
     * Adds a participant to an existing session.
     *
     * @param sessionId the session UUID
     * @param name the participant's display name (must be unique within the session)
     * @return the created participant
     * @throws ResponseStatusException 409 if the name is already taken
     */
    public Participant joinSession(String sessionId, String name) {
        PokerSession session = getSession(sessionId);
        if (participantRepository.findBySessionIdAndName(sessionId, name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name already taken");
        }
        Participant p = new Participant(name, session);
        return participantRepository.save(p);
    }

    /**
     * Records a participant's vote for the current round.
     *
     * @param sessionId the session UUID
     * @param name the participant's name
     * @param vote the point value chosen (e.g. "1", "5", "13", "?")
     * @throws ResponseStatusException 404 if participant not found
     */
    public void submitVote(String sessionId, String name, String vote) {
        Participant p = participantRepository.findBySessionIdAndName(sessionId, name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        p.setVote(vote);
        participantRepository.save(p);
    }

    /**
     * Reveals all votes in the session, making them visible to all participants.
     *
     * @param sessionId the session UUID
     * @return the updated session with revealed votes
     */
    public PokerSession reveal(String sessionId) {
        PokerSession session = getSession(sessionId);
        session.setRevealed(true);
        return sessionRepository.save(session);
    }

    /**
     * Resets the session for a new voting round, clearing all votes and optionally setting a new ticket.
     *
     * @param sessionId the session UUID
     * @param ticketId the JIRA ticket ID for the new round (nullable)
     * @return the reset session
     */
    public PokerSession reset(String sessionId, String ticketId) {
        PokerSession session = getSession(sessionId);
        session.setRevealed(false);
        session.setTicketId(ticketId);
        session.getParticipants().forEach(p -> p.setVote(null));
        return sessionRepository.save(session);
    }

    /**
     * Deletes all sessions created more than 24 hours ago.
     * Called by the scheduled cleanup task.
     */
    public void cleanupOldSessions() {
        List<PokerSession> old = sessionRepository.findByCreatedAtBefore(LocalDateTime.now().minusDays(1));
        sessionRepository.deleteAll(old);
    }
}
