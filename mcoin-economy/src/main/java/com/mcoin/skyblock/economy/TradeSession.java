package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.currency.CurrencyType;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TradeSession {
    private final UUID sessionId;
    private final UUID firstPlayerId;
    private final UUID secondPlayerId;
    private final EnumMap<CurrencyType, BigDecimal> firstPlayerOffer;
    private final EnumMap<CurrencyType, BigDecimal> secondPlayerOffer;
    private boolean firstPlayerConfirmed;
    private boolean secondPlayerConfirmed;
    private boolean completed;

    public TradeSession(final UUID sessionId, final UUID firstPlayerId, final UUID secondPlayerId) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.firstPlayerId = Objects.requireNonNull(firstPlayerId, "firstPlayerId");
        this.secondPlayerId = Objects.requireNonNull(secondPlayerId, "secondPlayerId");
        this.firstPlayerOffer = emptyOffer();
        this.secondPlayerOffer = emptyOffer();
    }

    public static TradeSession copyOf(final TradeSession tradeSession) {
        Objects.requireNonNull(tradeSession, "tradeSession");
        synchronized (tradeSession) {
            TradeSession copy = new TradeSession(tradeSession.sessionId, tradeSession.firstPlayerId, tradeSession.secondPlayerId);
            copy.firstPlayerOffer.putAll(tradeSession.firstPlayerOffer);
            copy.secondPlayerOffer.putAll(tradeSession.secondPlayerOffer);
            copy.firstPlayerConfirmed = tradeSession.firstPlayerConfirmed;
            copy.secondPlayerConfirmed = tradeSession.secondPlayerConfirmed;
            copy.completed = tradeSession.completed;
            return copy;
        }
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getFirstPlayerId() {
        return firstPlayerId;
    }

    public UUID getSecondPlayerId() {
        return secondPlayerId;
    }

    public synchronized Map<CurrencyType, BigDecimal> getFirstPlayerOffer() {
        return Collections.unmodifiableMap(new EnumMap<CurrencyType, BigDecimal>(firstPlayerOffer));
    }

    public synchronized Map<CurrencyType, BigDecimal> getSecondPlayerOffer() {
        return Collections.unmodifiableMap(new EnumMap<CurrencyType, BigDecimal>(secondPlayerOffer));
    }

    public synchronized boolean isFirstPlayerConfirmed() {
        return firstPlayerConfirmed;
    }

    public synchronized boolean isSecondPlayerConfirmed() {
        return secondPlayerConfirmed;
    }

    public synchronized boolean isCompleted() {
        return completed;
    }

    public synchronized void offer(final UUID playerId, final CurrencyType currencyType, final BigDecimal amount) {
        validateParticipant(playerId);
        BigDecimal safeAmount = requireAmount(amount);
        if (firstPlayerId.equals(playerId)) {
            firstPlayerOffer.put(Objects.requireNonNull(currencyType, "currencyType"), safeAmount);
        } else {
            secondPlayerOffer.put(Objects.requireNonNull(currencyType, "currencyType"), safeAmount);
        }
        firstPlayerConfirmed = false;
        secondPlayerConfirmed = false;
    }

    public synchronized void confirm(final UUID playerId) {
        validateParticipant(playerId);
        if (firstPlayerId.equals(playerId)) {
            firstPlayerConfirmed = true;
        } else {
            secondPlayerConfirmed = true;
        }
    }

    public synchronized boolean isReady() {
        return firstPlayerConfirmed && secondPlayerConfirmed && !completed;
    }

    public synchronized BigDecimal offeredAmount(final UUID playerId, final CurrencyType currencyType) {
        validateParticipant(playerId);
        if (firstPlayerId.equals(playerId)) {
            return firstPlayerOffer.get(Objects.requireNonNull(currencyType, "currencyType"));
        }
        return secondPlayerOffer.get(Objects.requireNonNull(currencyType, "currencyType"));
    }

    public synchronized void markCompleted() {
        this.completed = true;
    }

    private void validateParticipant(final UUID playerId) {
        UUID safePlayerId = Objects.requireNonNull(playerId, "playerId");
        if (!firstPlayerId.equals(safePlayerId) && !secondPlayerId.equals(safePlayerId)) {
            throw new IllegalArgumentException("player is not part of this trade");
        }
    }

    private static BigDecimal requireAmount(final BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return amount;
    }

    private static EnumMap<CurrencyType, BigDecimal> emptyOffer() {
        EnumMap<CurrencyType, BigDecimal> offer = new EnumMap<CurrencyType, BigDecimal>(CurrencyType.class);
        for (CurrencyType currencyType : CurrencyType.values()) {
            offer.put(currencyType, BigDecimal.ZERO);
        }
        return offer;
    }
}
