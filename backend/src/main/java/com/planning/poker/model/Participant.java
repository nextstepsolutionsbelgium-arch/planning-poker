package com.planning.poker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a participant in a planning poker session.
 * Each participant has a unique name within a session and can submit a single vote per round.
 */
@Entity
@Data
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String vote;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore
    private PokerSession session;

    public Participant(String name, PokerSession session) {
        this.name = name;
        this.session = session;
    }
}
