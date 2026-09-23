package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class ServerShopService {
    private final EconomyService economyService;
    private final Map<String, ShopOffer> offers = new LinkedHashMap<String, ShopOffer>();

    public ServerShopService(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
    }

    public synchronized void registerOffer(final ShopOffer offer) {
        ShopOffer safeOffer = Objects.requireNonNull(offer, "offer");
        offers.put(safeOffer.getItemKey(), safeOffer);
    }

    public synchronized Optional<ShopOffer> findOffer(final String itemKey) {
        return Optional.ofNullable(offers.get(itemKey));
    }

    public synchronized ShopTransactionReceipt buy(final UUID playerId, final String itemKey, final int quantity) {
        ShopOffer offer = requireOffer(itemKey);
        int safeQuantity = requireQuantity(quantity);
        BigDecimal totalPrice = offer.getBuyPrice().multiply(BigDecimal.valueOf(safeQuantity));
        economyService.withdraw(playerId, offer.getCurrencyType(), totalPrice);
        return new ShopTransactionReceipt(playerId, itemKey, offer.getCurrencyType(), offer.getBuyPrice(), totalPrice,
            safeQuantity, ShopTransactionType.BUY);
    }

    public synchronized ShopTransactionReceipt sell(final UUID playerId, final String itemKey, final int quantity) {
        ShopOffer offer = requireOffer(itemKey);
        int safeQuantity = requireQuantity(quantity);
        BigDecimal totalPrice = offer.getSellPrice().multiply(BigDecimal.valueOf(safeQuantity));
        economyService.deposit(playerId, offer.getCurrencyType(), totalPrice);
        return new ShopTransactionReceipt(playerId, itemKey, offer.getCurrencyType(), offer.getSellPrice(), totalPrice,
            safeQuantity, ShopTransactionType.SELL);
    }

    public synchronized Map<String, ShopOffer> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, ShopOffer>(offers));
    }

    private ShopOffer requireOffer(final String itemKey) {
        return findOffer(Objects.requireNonNull(itemKey, "itemKey"))
            .orElseThrow(() -> new IllegalArgumentException("Unknown shop offer: " + itemKey));
    }

    private static int requireQuantity(final int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return quantity;
    }
}
