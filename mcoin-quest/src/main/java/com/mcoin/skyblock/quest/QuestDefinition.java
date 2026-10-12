package com.mcoin.skyblock.quest;

import java.math.BigDecimal;
import java.util.Objects;

public final class QuestDefinition {
    private final String id;
    private final String title;
    private final String description;
    private final QuestCategory category;
    private final int targetAmount;
    private final BigDecimal rewardLidya;
    private final BigDecimal rewardMcoin;

    public QuestDefinition(final String id,
                           final String title,
                           final String description,
                           final QuestCategory category,
                           final int targetAmount,
                           final BigDecimal rewardLidya,
                           final BigDecimal rewardMcoin) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        this.category = Objects.requireNonNull(category, "category");
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("targetAmount must be positive");
        }
        this.targetAmount = targetAmount;
        this.rewardLidya = Objects.requireNonNull(rewardLidya, "rewardLidya");
        this.rewardMcoin = Objects.requireNonNull(rewardMcoin, "rewardMcoin");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getRewardLidya() {
        return rewardLidya;
    }

    public BigDecimal getRewardMcoin() {
        return rewardMcoin;
    }
}
