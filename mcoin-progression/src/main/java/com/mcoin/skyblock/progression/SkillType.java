package com.mcoin.skyblock.progression;

import java.util.Objects;

public enum SkillType {
    MINING("Mining", "Mine ores and stones"),
    FARMING("Farming", "Harvest crops and breed animals"),
    COMBAT("Combat", "Defeat monsters and bosses"),
    FORAGING("Foraging", "Chop trees and gather wood"),
    FISHING("Fishing", "Fish in island waters");

    private final String displayName;
    private final String description;

    SkillType(final String displayName, final String description) {
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.description = Objects.requireNonNull(description, "description");
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
