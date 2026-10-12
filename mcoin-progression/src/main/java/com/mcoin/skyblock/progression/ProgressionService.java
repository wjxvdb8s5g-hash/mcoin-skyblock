package com.mcoin.skyblock.progression;

import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ProgressionService {
    private final EconomyService economyService;
    private final Map<UUID, PlayerProgression> profiles = new LinkedHashMap<UUID, PlayerProgression>();

    public ProgressionService(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
    }

    public synchronized PlayerProgression getProgression(final UUID playerId) {
        final UUID safeId = Objects.requireNonNull(playerId, "playerId");
        PlayerProgression profile = profiles.get(safeId);
        if (profile == null) {
            profile = new PlayerProgression(safeId);
            profiles.put(safeId, profile);
        }
        return profile;
    }

    public synchronized boolean awardSkillXp(final UUID playerId, final SkillType skill, final double amount) {
        PlayerProgression profile = getProgression(playerId);
        return profile.addXp(skill, amount);
    }

    public synchronized void purchaseRank(final UUID playerId, final RankType targetRank) {
        Objects.requireNonNull(targetRank, "targetRank");
        if (targetRank == RankType.MEMBER) {
            throw new IllegalArgumentException("Cannot purchase default Member rank.");
        }
        PlayerProgression profile = getProgression(playerId);
        if (profile.getRank().ordinal() >= targetRank.ordinal()) {
            throw new IllegalStateException("You already have rank " + profile.getRank().getDisplayName() + " or higher.");
        }

        BigDecimal cost = targetRank.getCostMcoin();
        economyService.withdraw(playerId, CurrencyType.MCOIN, cost);
        profile.setRank(targetRank);
    }

    public synchronized void prestige(final UUID playerId) {
        PlayerProgression profile = getProgression(playerId);
        if (profile.getTotalSkillLevel() < 100) {
            throw new IllegalStateException("You need a total skill level of at least 100 to prestige. Current: " + profile.getTotalSkillLevel());
        }
        profile.incrementPrestige();
    }

    public synchronized Map<UUID, PlayerProgression> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<UUID, PlayerProgression>(profiles));
    }
}
