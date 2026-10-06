package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class TradeService {
    private final EconomyService economyService;
    private final Map<UUID, TradeSession> sessions = new LinkedHashMap<UUID, TradeSession>();

    public TradeService(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
    }

    public synchronized TradeSession createSession(final UUID firstPlayerId, final UUID secondPlayerId) {
        UUID safeFirstPlayerId = Objects.requireNonNull(firstPlayerId, "firstPlayerId");
        UUID safeSecondPlayerId = Objects.requireNonNull(secondPlayerId, "secondPlayerId");
        if (safeFirstPlayerId.equals(safeSecondPlayerId)) {
            throw new IllegalArgumentException("trade participants must differ");
        }
        TradeSession session = new TradeSession(UUID.randomUUID(), safeFirstPlayerId, safeSecondPlayerId);
        sessions.put(session.getSessionId(), session);
        return TradeSession.copyOf(session);
    }

    public synchronized Optional<TradeSession> findSession(final UUID sessionId) {
        TradeSession session = sessions.get(Objects.requireNonNull(sessionId, "sessionId"));
        return session == null ? Optional.<TradeSession>empty() : Optional.of(TradeSession.copyOf(session));
    }

    public synchronized void offer(final UUID sessionId,
                                   final UUID playerId,
                                   final CurrencyType currencyType,
                                   final BigDecimal amount) {
        requireSession(sessionId).offer(playerId, currencyType, amount);
    }

    public synchronized void confirm(final UUID sessionId, final UUID playerId) {
        requireSession(sessionId).confirm(playerId);
    }

    public synchronized TradeSession execute(final UUID sessionId) {
        TradeSession session = requireSession(sessionId);
        if (!session.isReady()) {
            throw new IllegalStateException("trade session is not fully confirmed");
        }

        synchronized (economyService) {
            CurrencyAccount firstWallet = economyService.account(session.getFirstPlayerId());
            CurrencyAccount secondWallet = economyService.account(session.getSecondPlayerId());
            validateTradeFunds(session, firstWallet, secondWallet);
            exchangeOffers(session, firstWallet, secondWallet);
        }

        session.markCompleted();
        return TradeSession.copyOf(session);
    }

    public synchronized Map<UUID, TradeSession> snapshot() {
        Map<UUID, TradeSession> copy = new LinkedHashMap<UUID, TradeSession>(sessions.size());
        for (Map.Entry<UUID, TradeSession> entry : sessions.entrySet()) {
            copy.put(entry.getKey(), TradeSession.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    private void validateTradeFunds(final TradeSession session,
                                    final CurrencyAccount firstWallet,
                                    final CurrencyAccount secondWallet) {
        for (CurrencyType currencyType : CurrencyType.values()) {
            BigDecimal firstAmount = session.offeredAmount(session.getFirstPlayerId(), currencyType);
            BigDecimal secondAmount = session.offeredAmount(session.getSecondPlayerId(), currencyType);
            if (firstWallet.getBalance(currencyType).compareTo(firstAmount) < 0) {
                throw new IllegalArgumentException("insufficient funds for first player in " + currencyType);
            }
            if (secondWallet.getBalance(currencyType).compareTo(secondAmount) < 0) {
                throw new IllegalArgumentException("insufficient funds for second player in " + currencyType);
            }
        }
    }

    private void exchangeOffers(final TradeSession session,
                                final CurrencyAccount firstWallet,
                                final CurrencyAccount secondWallet) {
        for (CurrencyType currencyType : CurrencyType.values()) {
            BigDecimal firstAmount = session.offeredAmount(session.getFirstPlayerId(), currencyType);
            BigDecimal secondAmount = session.offeredAmount(session.getSecondPlayerId(), currencyType);
            if (firstAmount.compareTo(BigDecimal.ZERO) > 0) {
                firstWallet.withdraw(currencyType, firstAmount);
                secondWallet.deposit(currencyType, firstAmount);
            }
            if (secondAmount.compareTo(BigDecimal.ZERO) > 0) {
                secondWallet.withdraw(currencyType, secondAmount);
                firstWallet.deposit(currencyType, secondAmount);
            }
        }
    }

    private TradeSession requireSession(final UUID sessionId) {
        TradeSession session = sessions.get(Objects.requireNonNull(sessionId, "sessionId"));
        if (session == null) {
            throw new IllegalArgumentException("unknown trade session: " + sessionId);
        }
        if (session.isCompleted()) {
            throw new IllegalStateException("trade session already completed");
        }
        return session;
    }
}
