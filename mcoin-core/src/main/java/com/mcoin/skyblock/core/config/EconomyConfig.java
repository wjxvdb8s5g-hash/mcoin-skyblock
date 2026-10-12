package com.mcoin.skyblock.core.config;

import java.math.BigDecimal;
import java.util.Objects;

public final class EconomyConfig {
    private final BigDecimal startingMcoin;
    private final BigDecimal startingLidya;
    private final BigDecimal transferTaxRate;
    private final BigDecimal bankInterestRate;

    public EconomyConfig(final BigDecimal startingMcoin,
                         final BigDecimal startingLidya,
                         final BigDecimal transferTaxRate,
                         final BigDecimal bankInterestRate) {
        this.startingMcoin = Objects.requireNonNull(startingMcoin, "startingMcoin");
        this.startingLidya = Objects.requireNonNull(startingLidya, "startingLidya");
        this.transferTaxRate = Objects.requireNonNull(transferTaxRate, "transferTaxRate");
        this.bankInterestRate = Objects.requireNonNull(bankInterestRate, "bankInterestRate");
    }

    public static EconomyConfig defaultConfig() {
        return new EconomyConfig(
            new BigDecimal("100.00"),
            new BigDecimal("1000.00"),
            new BigDecimal("0.00"),
            new BigDecimal("0.01")
        );
    }

    public BigDecimal getStartingMcoin() {
        return startingMcoin;
    }

    public BigDecimal getStartingLidya() {
        return startingLidya;
    }

    public BigDecimal getTransferTaxRate() {
        return transferTaxRate;
    }

    public BigDecimal getBankInterestRate() {
        return bankInterestRate;
    }
}
