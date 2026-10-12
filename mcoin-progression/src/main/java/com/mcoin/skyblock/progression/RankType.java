package com.mcoin.skyblock.progression;

import java.math.BigDecimal;
import java.util.Objects;

public enum RankType {
    MEMBER("Member", "&7", BigDecimal.ZERO, 1.0),
    VIP("VIP", "&a[VIP]", new BigDecimal("50.00"), 1.10),
    VIP_PLUS("VIP+", "&a[VIP&6+&a]", new BigDecimal("100.00"), 1.20),
    MVP("MVP", "&b[MVP]", new BigDecimal("250.00"), 1.35),
    MVP_PLUS("MVP+", "&b[MVP&c+&b]", new BigDecimal("500.00"), 1.50);

    private final String displayName;
    private final String prefix;
    private final BigDecimal costMcoin;
    private final double boostMultiplier;

    RankType(final String displayName, final String prefix, final BigDecimal costMcoin, final double boostMultiplier) {
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.prefix = Objects.requireNonNull(prefix, "prefix");
        this.costMcoin = Objects.requireNonNull(costMcoin, "costMcoin");
        this.boostMultiplier = boostMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public BigDecimal getCostMcoin() {
        return costMcoin;
    }

    public double getBoostMultiplier() {
        return boostMultiplier;
    }
}
