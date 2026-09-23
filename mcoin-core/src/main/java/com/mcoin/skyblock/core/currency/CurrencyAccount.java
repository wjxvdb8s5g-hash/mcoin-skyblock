package com.mcoin.skyblock.core.currency;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CurrencyAccount {
    private final UUID playerId;
    private final EnumMap<CurrencyType, BigDecimal> balances = new EnumMap<CurrencyType, BigDecimal>(CurrencyType.class);

    public CurrencyAccount(final UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        for (CurrencyType type : CurrencyType.values()) {
            balances.put(type, BigDecimal.ZERO);
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public BigDecimal getBalance(final CurrencyType type) {
        return balances.get(requireType(type));
    }

    public void deposit(final CurrencyType type, final BigDecimal amount) {
        balances.put(requireType(type), getBalance(type).add(requireAmount(amount)));
    }

    public void withdraw(final CurrencyType type, final BigDecimal amount) {
        final CurrencyType safeType = requireType(type);
        final BigDecimal safeAmount = requireAmount(amount);
        if (getBalance(safeType).compareTo(safeAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for " + safeType);
        }
        balances.put(safeType, getBalance(safeType).subtract(safeAmount));
    }

    public Map<CurrencyType, BigDecimal> balancesView() {
        return new EnumMap<CurrencyType, BigDecimal>(balances);
    }

    private static CurrencyType requireType(final CurrencyType type) {
        return Objects.requireNonNull(type, "type");
    }

    private static BigDecimal requireAmount(final BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return amount;
    }
}
