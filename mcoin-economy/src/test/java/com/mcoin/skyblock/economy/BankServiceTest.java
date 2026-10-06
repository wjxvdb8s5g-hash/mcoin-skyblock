package com.mcoin.skyblock.economy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BankServiceTest {
    @Test
    void movesFundsBetweenWalletAndBankBalances() {
        EconomyService economyService = new EconomyService();
        BankService bankService = new BankService(economyService);
        UUID playerId = UUID.randomUUID();

        economyService.deposit(playerId, CurrencyType.LIDYA, new BigDecimal("500"));
        bankService.deposit(playerId, CurrencyType.LIDYA, new BigDecimal("125"));
        bankService.withdraw(playerId, CurrencyType.LIDYA, new BigDecimal("25"));

        assertEquals(new BigDecimal("400"), economyService.account(playerId).getBalance(CurrencyType.LIDYA));
        assertEquals(new BigDecimal("100"), bankService.balance(playerId, CurrencyType.LIDYA));
    }

    @Test
    void snapshotReturnsDetachedBankAccounts() {
        EconomyService economyService = new EconomyService();
        BankService bankService = new BankService(economyService);
        UUID playerId = UUID.randomUUID();

        economyService.deposit(playerId, CurrencyType.MCOIN, new BigDecimal("50"));
        bankService.deposit(playerId, CurrencyType.MCOIN, new BigDecimal("10"));
        CurrencyAccount snapshot = bankService.snapshot().get(playerId);
        CurrencyAccount live = bankService.snapshot().get(playerId);
        snapshot.withdraw(CurrencyType.MCOIN, new BigDecimal("5"));

        assertNotSame(snapshot, live);
        assertEquals(new BigDecimal("10"), bankService.balance(playerId, CurrencyType.MCOIN));
        assertEquals(new BigDecimal("5"), snapshot.getBalance(CurrencyType.MCOIN));
    }
}
