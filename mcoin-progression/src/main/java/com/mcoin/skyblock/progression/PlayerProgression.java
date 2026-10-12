package com.mcoin.skyblock.progression;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerProgression {
    private final UUID playerId;
    private RankType rank;
    private int prestige;
    private final EnumMap<SkillType, Integer> levels = new EnumMap<SkillType, Integer>(SkillType.class);
    private final EnumMap<SkillType, Double> experience = new EnumMap<SkillType, Double>(SkillType.class);

    public PlayerProgression(final UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.rank = RankType.MEMBER;
        this.prestige = 0;
        for (SkillType skill : SkillType.values()) {
            levels.put(skill, 1);
            experience.put(skill, 0.0);
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public synchronized RankType getRank() {
        return rank;
    }

    public synchronized void setRank(final RankType rank) {
        this.rank = Objects.requireNonNull(rank, "rank");
    }

    public synchronized int getPrestige() {
        return prestige;
    }

    public synchronized void incrementPrestige() {
        this.prestige++;
        for (SkillType skill : SkillType.values()) {
            levels.put(skill, 1);
            experience.put(skill, 0.0);
        }
    }

    public synchronized int getLevel(final SkillType skill) {
        return levels.get(Objects.requireNonNull(skill, "skill"));
    }

    public synchronized double getXp(final SkillType skill) {
        return experience.get(Objects.requireNonNull(skill, "skill"));
    }

    public synchronized boolean addXp(final SkillType skill, final double amount) {
        if (amount <= 0) {
            return false;
        }
        SkillType safeSkill = Objects.requireNonNull(skill, "skill");
        int currentLvl = levels.get(safeSkill);
        double currentXp = experience.get(safeSkill) + amount;
        boolean leveledUp = false;

        while (currentXp >= xpForLevel(currentLvl + 1) && currentLvl < 100) {
            currentXp -= xpForLevel(currentLvl + 1);
            currentLvl++;
            leveledUp = true;
        }

        levels.put(safeSkill, currentLvl);
        experience.put(safeSkill, currentXp);
        return leveledUp;
    }

    public static double xpForLevel(final int level) {
        return level * 100.0;
    }

    public synchronized Map<SkillType, Integer> getLevelsView() {
        return Collections.unmodifiableMap(new EnumMap<SkillType, Integer>(levels));
    }

    public synchronized Map<SkillType, Double> getExperienceView() {
        return Collections.unmodifiableMap(new EnumMap<SkillType, Double>(experience));
    }

    public synchronized int getTotalSkillLevel() {
        int total = 0;
        for (int lvl : levels.values()) {
            total += lvl;
        }
        return total;
    }
}
