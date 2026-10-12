package com.mcoin.skyblock.quest;

import java.util.Objects;
import java.util.UUID;

public final class QuestProgress {
    private final UUID playerId;
    private final String questId;
    private int currentAmount;
    private boolean completed;
    private boolean claimed;

    public QuestProgress(final UUID playerId, final String questId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.questId = Objects.requireNonNull(questId, "questId");
        this.currentAmount = 0;
        this.completed = false;
        this.claimed = false;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getQuestId() {
        return questId;
    }

    public synchronized int getCurrentAmount() {
        return currentAmount;
    }

    public synchronized boolean isCompleted() {
        return completed;
    }

    public synchronized boolean isClaimed() {
        return claimed;
    }

    public synchronized boolean addProgress(final int amount, final int targetAmount) {
        if (amount <= 0 || completed) {
            return false;
        }
        this.currentAmount += amount;
        if (this.currentAmount >= targetAmount) {
            this.currentAmount = targetAmount;
            this.completed = true;
            return true;
        }
        return false;
    }

    public synchronized void markClaimed() {
        if (!completed) {
            throw new IllegalStateException("Cannot claim uncompleted quest.");
        }
        if (claimed) {
            throw new IllegalStateException("Quest reward has already been claimed.");
        }
        this.claimed = true;
    }
}
