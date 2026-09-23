package com.mcoin.skyblock.core.currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class EconomyServiceTest {
    @Test
    void supportsIndependentMcoinAndLidyaBalancesWithTransfers() {
        EconomyService service = new EconomyService();
        UUID firstPlayer = UUID.randomUUID();
        UUID secondPlayer = UUID.randomUUID();

        service.deposit(firstPlayer, CurrencyType.MCOIN, new BigDecimal("150"));
        service.deposit(firstPlayer, CurrencyType.LIDYA, new BigDecimal("1000"));
        service.transfer(firstPlayer, secondPlayer, CurrencyType.MCOIN, new BigDecimal("25"));

        assertEquals(new BigDecimal("125"), service.account(firstPlayer).getBalance(CurrencyType.MCOIN));
        assertEquals(new BigDecimal("1000"), service.account(firstPlayer).getBalance(CurrencyType.LIDYA));
        assertEquals(new BigDecimal("25"), service.account(secondPlayer).getBalance(CurrencyType.MCOIN));
    }

    @Test
    void rejectsOverdrafts() {
        EconomyService service = new EconomyService();
        UUID player = UUID.randomUUID();
        service.deposit(player, CurrencyType.LIDYA, new BigDecimal("10"));

        assertThrows(IllegalArgumentException.class,
            () -> service.withdraw(player, CurrencyType.LIDYA, new BigDecimal("11")));
    }

    @Test
    void rejectsSelfTransfers() {
        EconomyService service = new EconomyService();
        UUID player = UUID.randomUUID();
        service.deposit(player, CurrencyType.MCOIN, new BigDecimal("10"));

        assertThrows(IllegalArgumentException.class,
            () -> service.transfer(player, player, CurrencyType.MCOIN, new BigDecimal("1")));
    }

    @Test
    void keepsTransferBalancesConsistentUnderConcurrentLoad() throws InterruptedException {
        EconomyService service = new EconomyService();
        UUID firstPlayer = UUID.randomUUID();
        UUID secondPlayer = UUID.randomUUID();
        int transferCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(transferCount);
        CountDownLatch ready = new CountDownLatch(transferCount);
        CountDownLatch start = new CountDownLatch(1);

        service.deposit(firstPlayer, CurrencyType.MCOIN, new BigDecimal("10"));

        for (int index = 0; index < transferCount; index++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await(5, TimeUnit.SECONDS);
                    service.transfer(firstPlayer, secondPlayer, CurrencyType.MCOIN, BigDecimal.ONE);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(exception);
                }
            });
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(BigDecimal.ZERO, service.account(firstPlayer).getBalance(CurrencyType.MCOIN));
        assertEquals(new BigDecimal("10"), service.account(secondPlayer).getBalance(CurrencyType.MCOIN));
    }
}
