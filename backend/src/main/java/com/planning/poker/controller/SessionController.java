package com.planning.poker.controller;

import com.planning.poker.model.Participant;
import com.planning.poker.model.PokerSession;
import com.planning.poker.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing planning poker sessions.
 * Provides endpoints to create, join, vote, reveal, and reset sessions.
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Planning poker session management")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Create a new poker session", description = "Returns a session with a unique UUID that can be shared with participants")
    public ResponseEntity<PokerSession> createSession() {
        return ResponseEntity.ok(sessionService.createSession());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session state", description = "Returns session details. Votes are hidden until revealed.")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String id) {
        PokerSession session = sessionService.getSession(id);
        return ResponseEntity.ok(new SessionResponse(session));
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a session", description = "Adds a participant to the session. Name must be unique within the session.")
    public ResponseEntity<Participant> join(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(sessionService.joinSession(id, body.get("name")));
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Submit a vote", description = "Records the participant's point estimate for the current round")
    public ResponseEntity<Void> vote(@PathVariable String id, @RequestBody Map<String, String> body) {
        sessionService.submitVote(id, body.get("name"), body.get("vote"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reveal")
    @Operation(summary = "Reveal votes", description = "Makes all votes visible to all participants")
    public ResponseEntity<SessionResponse> reveal(@PathVariable String id) {
        PokerSession session = sessionService.reveal(id);
        return ResponseEntity.ok(new SessionResponse(session));
    }

    @PostMapping("/{id}/reset")
    @Operation(summary = "Reset for next round", description = "Clears all votes and optionally sets a new JIRA ticket ID")
    public ResponseEntity<SessionResponse> reset(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        String ticketId = body != null ? body.get("ticketId") : null;
        PokerSession session = sessionService.reset(id, ticketId);
        return ResponseEntity.ok(new SessionResponse(session));
    }

    /** View of a participant with vote hidden when not revealed. */
    record ParticipantView(String name, String vote, boolean hasVoted) {}

    /** Session response DTO that conditionally hides votes until revealed. */
    record SessionResponse(String id, String ticketId, boolean revealed, java.util.List<ParticipantView> participants) {
        SessionResponse(PokerSession s) {
            this(s.getId(), s.getTicketId(), s.isRevealed(),
                s.getParticipants().stream().map(p -> new ParticipantView(
                    p.getName(),
                    s.isRevealed() ? p.getVote() : null,
                    p.getVote() != null
                )).toList());
        }
    }
}
