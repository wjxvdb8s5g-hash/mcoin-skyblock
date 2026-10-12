package com.mcoin.skyblock.spawner;

import java.math.BigDecimal;
import java.util.Objects;

public enum SpawnerType {
    PIG("Pig", new BigDecimal("500.00"), 1),
    COW("Cow", new BigDecimal("1000.00"), 1),
    CHICKEN("Chicken", new BigDecimal("750.00"), 1),
    ZOMBIE("Zombie", new BigDecimal("2500.00"), 2),
    SKELETON("Skeleton", new BigDecimal("5000.00"), 2),
    SPIDER("Spider", new BigDecimal("4000.00"), 2),
    BLAZE("Blaze", new BigDecimal("15000.00"), 3),
    IRON_GOLEM("Iron Golem", new BigDecimal("50000.00"), 4);

    private final String displayName;
    private final BigDecimal baseCost;
    private final int minIslandLevel;

    SpawnerType(final String displayName, final BigDecimal baseCost, final int minIslandLevel) {
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.baseCost = Objects.requireNonNull(baseCost, "baseCost");
        this.minIslandLevel = minIslandLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public int getMinIslandLevel() {
        return minIslandLevel;
    }
}
