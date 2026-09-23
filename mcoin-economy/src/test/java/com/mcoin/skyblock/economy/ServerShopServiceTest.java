package com.mcoin.skyblock.economy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServerShopServiceTest {
    @Test
    void buysAndSellsAgainstPlayerWalletBalance() {
        EconomyService economyService = new EconomyService();
        ServerShopService shopService = new ServerShopService(economyService);
        UUID playerId = UUID.randomUUID();

        economyService.deposit(playerId, CurrencyType.LIDYA, new BigDecimal("1000"));
        shopService.registerOffer(new ShopOffer("spawner_zombie", CurrencyType.LIDYA,
            new BigDecimal("120"), new BigDecimal("80")));

        ShopTransactionReceipt buyReceipt = shopService.buy(playerId, "spawner_zombie", 2);
        ShopTransactionReceipt sellReceipt = shopService.sell(playerId, "spawner_zombie", 1);

        assertEquals(ShopTransactionType.BUY, buyReceipt.getTransactionType());
        assertEquals(new BigDecimal("240"), buyReceipt.getTotalPrice());
        assertEquals(ShopTransactionType.SELL, sellReceipt.getTransactionType());
        assertEquals(new BigDecimal("840"), economyService.account(playerId).getBalance(CurrencyType.LIDYA));
    }

    @Test
    void rejectsUnknownOffers() {
        ServerShopService shopService = new ServerShopService(new EconomyService());
        assertThrows(IllegalArgumentException.class, () -> shopService.buy(UUID.randomUUID(), "missing", 1));
    }
}
