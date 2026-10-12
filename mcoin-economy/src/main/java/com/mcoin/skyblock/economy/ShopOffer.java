package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.currency.CurrencyType;
import java.math.BigDecimal;
import java.util.Objects;

public final class ShopOffer {
    private final String itemKey;
    private final CurrencyType currencyType;
    private final BigDecimal buyPrice;
    private final BigDecimal sellPrice;

    public ShopOffer(final String itemKey,
                     final CurrencyType currencyType,
                     final BigDecimal buyPrice,
                     final BigDecimal sellPrice) {
        this.itemKey = requireText(itemKey, "itemKey");
        this.currencyType = Objects.requireNonNull(currencyType, "currencyType");
        this.buyPrice = requireAmount(buyPrice, "buyPrice");
        this.sellPrice = requireAmount(sellPrice, "sellPrice");
    }

    public String getItemKey() {
        return itemKey;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    private static String requireText(final String value, final String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    private static BigDecimal requireAmount(final BigDecimal amount, final String name) {
        Objects.requireNonNull(amount, name);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return amount;
    }
}
