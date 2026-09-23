package com.mcoin.skyblock.economy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TradeServiceTest {
    @Test
    void executesConfirmedTwoWayTradesAcrossCurrencies() {
        EconomyService economyService = new EconomyService();
        TradeService tradeService = new TradeService(economyService);
        UUID firstPlayerId = UUID.randomUUID();
        UUID secondPlayerId = UUID.randomUUID();

        economyService.deposit(firstPlayerId, CurrencyType.MCOIN, new BigDecimal("20"));
        economyService.deposit(secondPlayerId, CurrencyType.LIDYA, new BigDecimal("400"));

        TradeSession session = tradeService.createSession(firstPlayerId, secondPlayerId);
        tradeService.offer(session.getSessionId(), firstPlayerId, CurrencyType.MCOIN, new BigDecimal("5"));
        tradeService.offer(session.getSessionId(), secondPlayerId, CurrencyType.LIDYA, new BigDecimal("125"));
        tradeService.confirm(session.getSessionId(), firstPlayerId);
        tradeService.confirm(session.getSessionId(), secondPlayerId);

        TradeSession completed = tradeService.execute(session.getSessionId());

        assertEquals(new BigDecimal("15"), economyService.account(firstPlayerId).getBalance(CurrencyType.MCOIN));
        assertEquals(new BigDecimal("125"), economyService.account(firstPlayerId).getBalance(CurrencyType.LIDYA));
        assertEquals(new BigDecimal("5"), economyService.account(secondPlayerId).getBalance(CurrencyType.MCOIN));
        assertEquals(new BigDecimal("275"), economyService.account(secondPlayerId).getBalance(CurrencyType.LIDYA));
        assertTrue(completed.isCompleted());
    }

    @Test
    void rejectsExecutionWithoutBothConfirmations() {
        EconomyService economyService = new EconomyService();
        TradeService tradeService = new TradeService(economyService);
        UUID firstPlayerId = UUID.randomUUID();
        UUID secondPlayerId = UUID.randomUUID();

        TradeSession session = tradeService.createSession(firstPlayerId, secondPlayerId);
        tradeService.offer(session.getSessionId(), firstPlayerId, CurrencyType.MCOIN, BigDecimal.ONE);
        tradeService.confirm(session.getSessionId(), firstPlayerId);

        assertThrows(IllegalStateException.class, () -> tradeService.execute(session.getSessionId()));
    }
}
