package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.currency.CurrencyType;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class ShopTransactionReceipt {
    private final UUID playerId;
    private final String itemKey;
    private final CurrencyType currencyType;
    private final BigDecimal unitPrice;
    private final BigDecimal totalPrice;
    private final int quantity;
    private final ShopTransactionType transactionType;

    public ShopTransactionReceipt(final UUID playerId,
                                  final String itemKey,
                                  final CurrencyType currencyType,
                                  final BigDecimal unitPrice,
                                  final BigDecimal totalPrice,
                                  final int quantity,
                                  final ShopTransactionType transactionType) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.itemKey = Objects.requireNonNull(itemKey, "itemKey");
        this.currencyType = Objects.requireNonNull(currencyType, "currencyType");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice");
        this.totalPrice = Objects.requireNonNull(totalPrice, "totalPrice");
        this.quantity = quantity;
        this.transactionType = Objects.requireNonNull(transactionType, "transactionType");
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getItemKey() {
        return itemKey;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public ShopTransactionType getTransactionType() {
        return transactionType;
    }
}
