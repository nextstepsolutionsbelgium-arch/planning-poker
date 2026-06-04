package com.planning.poker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a planning poker session.
 * Each session has a unique UUID that is never reused, tracks a JIRA ticket,
 * and holds a list of participants with their votes.
 * Sessions are automatically cleaned up 24 hours after creation.
 */
@Entity
@Data
@NoArgsConstructor
public class PokerSession {

    @Id
    private String id = UUID.randomUUID().toString();

    private String ticketId;
    private boolean revealed = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();
}
