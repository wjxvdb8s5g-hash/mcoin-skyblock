package com.mcoin.skyblock.leaderboard;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class LeaderboardEntry {
    private final int rank;
    private final UUID id;
    private final String label;
    private final BigDecimal score;
    private final String displayScore;

    public LeaderboardEntry(final int rank,
                            final UUID id,
                            final String label,
                            final BigDecimal score,
                            final String displayScore) {
        this.rank = rank;
        this.id = Objects.requireNonNull(id, "id");
        this.label = Objects.requireNonNull(label, "label");
        this.score = Objects.requireNonNull(score, "score");
        this.displayScore = Objects.requireNonNull(displayScore, "displayScore");
    }

    public int getRank() {
        return rank;
    }

    public UUID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getDisplayScore() {
        return displayScore;
    }
}
