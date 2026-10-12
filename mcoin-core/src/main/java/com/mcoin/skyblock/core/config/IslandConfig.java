package com.mcoin.skyblock.core.config;

import java.math.BigDecimal;
import java.util.Objects;

public final class IslandConfig {
    private final String defaultBiome;
    private final int defaultSize;
    private final int maxIslandSize;
    private final int maxCoopMembers;
    private final BigDecimal upgradeCostPerLevel;
    private final BigDecimal expansionCostPerBlock;
    private final BigDecimal biomeChangeCost;

    public IslandConfig(final String defaultBiome,
                        final int defaultSize,
                        final int maxIslandSize,
                        final int maxCoopMembers,
                        final BigDecimal upgradeCostPerLevel,
                        final BigDecimal expansionCostPerBlock,
                        final BigDecimal biomeChangeCost) {
        this.defaultBiome = Objects.requireNonNull(defaultBiome, "defaultBiome");
        this.defaultSize = defaultSize;
        this.maxIslandSize = maxIslandSize;
        this.maxCoopMembers = maxCoopMembers;
        this.upgradeCostPerLevel = Objects.requireNonNull(upgradeCostPerLevel, "upgradeCostPerLevel");
        this.expansionCostPerBlock = Objects.requireNonNull(expansionCostPerBlock, "expansionCostPerBlock");
        this.biomeChangeCost = Objects.requireNonNull(biomeChangeCost, "biomeChangeCost");
    }

    public static IslandConfig defaultConfig() {
        return new IslandConfig(
            "PLAINS",
            100,
            500,
            8,
            new BigDecimal("500.00"),
            new BigDecimal("10.00"),
            new BigDecimal("250.00")
        );
    }

    public String getDefaultBiome() {
        return defaultBiome;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public int getMaxIslandSize() {
        return maxIslandSize;
    }

    public int getMaxCoopMembers() {
        return maxCoopMembers;
    }

    public BigDecimal getUpgradeCostPerLevel() {
        return upgradeCostPerLevel;
    }

    public BigDecimal getExpansionCostPerBlock() {
        return expansionCostPerBlock;
    }

    public BigDecimal getBiomeChangeCost() {
        return biomeChangeCost;
    }
}
