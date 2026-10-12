package com.mcoin.skyblock.quest;

import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class QuestService {
    private final EconomyService economyService;
    private final Map<String, QuestDefinition> definitions = new LinkedHashMap<String, QuestDefinition>();
    private final Map<UUID, Map<String, QuestProgress>> progress = new LinkedHashMap<UUID, Map<String, QuestProgress>>();

    public QuestService(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
        registerDefaults();
    }

    private void registerDefaults() {
        registerQuest(new QuestDefinition("daily_mining", "Ore Digger", "Mine 128 stone or ore blocks", QuestCategory.DAILY, 128, new BigDecimal("250.00"), new BigDecimal("5.00")));
        registerQuest(new QuestDefinition("daily_harvest", "Master Harvester", "Harvest 256 wheat, carrot or potato crops", QuestCategory.DAILY, 256, new BigDecimal("350.00"), new BigDecimal("5.00")));
        registerQuest(new QuestDefinition("daily_slayer", "Mob Slayer", "Defeat 50 hostile mobs", QuestCategory.DAILY, 50, new BigDecimal("500.00"), new BigDecimal("10.00")));
        registerQuest(new QuestDefinition("weekly_expand", "Island Expansionist", "Expand your island by 50 blocks", QuestCategory.WEEKLY, 50, new BigDecimal("2500.00"), new BigDecimal("25.00")));
        registerQuest(new QuestDefinition("challenge_millionaire", "Lidya Tycoon", "Accumulate 1,000,000 Lidya in total transactions", QuestCategory.CHALLENGE, 1000, new BigDecimal("10000.00"), new BigDecimal("100.00")));
    }

    public synchronized void registerQuest(final QuestDefinition quest) {
        Objects.requireNonNull(quest, "quest");
        definitions.put(quest.getId(), quest);
    }

    public synchronized Optional<QuestDefinition> findQuest(final String questId) {
        return Optional.ofNullable(definitions.get(questId));
    }

    public synchronized List<QuestDefinition> getQuests() {
        return Collections.unmodifiableList(new ArrayList<QuestDefinition>(definitions.values()));
    }

    public synchronized QuestProgress getProgress(final UUID playerId, final String questId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(questId, "questId");
        Map<String, QuestProgress> playerQuests = progress.get(playerId);
        if (playerQuests == null) {
            playerQuests = new LinkedHashMap<String, QuestProgress>();
            progress.put(playerId, playerQuests);
        }
        QuestProgress qp = playerQuests.get(questId);
        if (qp == null) {
            qp = new QuestProgress(playerId, questId);
            playerQuests.put(questId, qp);
        }
        return qp;
    }

    public synchronized boolean recordProgress(final UUID playerId, final String questId, final int amount) {
        QuestDefinition def = definitions.get(questId);
        if (def == null) {
            return false;
        }
        QuestProgress qp = getProgress(playerId, questId);
        return qp.addProgress(amount, def.getTargetAmount());
    }

    public synchronized void claimReward(final UUID playerId, final String questId) {
        QuestDefinition def = definitions.get(questId);
        if (def == null) {
            throw new IllegalArgumentException("Unknown quest: " + questId);
        }
        QuestProgress qp = getProgress(playerId, questId);
        qp.markClaimed();

        if (def.getRewardLidya().compareTo(BigDecimal.ZERO) > 0) {
            economyService.deposit(playerId, CurrencyType.LIDYA, def.getRewardLidya());
        }
        if (def.getRewardMcoin().compareTo(BigDecimal.ZERO) > 0) {
            economyService.deposit(playerId, CurrencyType.MCOIN, def.getRewardMcoin());
        }
    }
}
