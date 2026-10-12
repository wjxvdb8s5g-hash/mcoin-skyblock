package com.mcoin.skyblock.economy.command;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.SimplePlayerCommandSender;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.economy.BankService;
import com.mcoin.skyblock.economy.ServerShopService;
import com.mcoin.skyblock.economy.ShopOffer;
import com.mcoin.skyblock.economy.ShopTransactionType;
import com.mcoin.skyblock.economy.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EconomyCommandTest {

    private EconomyService economyService;
    private BankService bankService;
    private ServerShopService shopService;
    private TradeService tradeService;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        economyService = new EconomyService();
        bankService = new BankService(economyService);
        shopService = new ServerShopService(economyService);
        tradeService = new TradeService(economyService);
        dispatcher = new CommandDispatcher();

        dispatcher.register(new MCoinCurrencyCommand(economyService, CurrencyType.MCOIN));
        dispatcher.register(new MCoinCurrencyCommand(economyService, CurrencyType.LIDYA));
        dispatcher.register(new BankCommand(bankService));
        dispatcher.register(new ShopCommand(shopService));
        dispatcher.register(new TradeCommand(tradeService));
    }

    @Test
    void testMCoinCurrencyCommand() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        SimplePlayerCommandSender s1 = new SimplePlayerCommandSender(p1, "PlayerOne");
        s1.addPermission("mcoin.economy.mcoin");

        economyService.deposit(p1, CurrencyType.MCOIN, BigDecimal.valueOf(100));

        // Check balance
        CommandResult balRes = dispatcher.dispatch(s1, "mcoin bal");
        assertTrue(balRes.isSuccess());

        // Pay command
        CommandResult payRes = dispatcher.dispatch(s1, "mcoin pay " + p2 + " 25");
        assertTrue(payRes.isSuccess());
        assertEquals(0, BigDecimal.valueOf(75).compareTo(economyService.account(p1).getBalance(CurrencyType.MCOIN)));
        assertEquals(0, BigDecimal.valueOf(25).compareTo(economyService.account(p2).getBalance(CurrencyType.MCOIN)));
    }

    @Test
    void testBankCommand() {
        UUID p1 = UUID.randomUUID();
        SimplePlayerCommandSender s1 = new SimplePlayerCommandSender(p1, "Banker");
        s1.addPermission("mcoin.economy.bank");

        economyService.deposit(p1, CurrencyType.LIDYA, BigDecimal.valueOf(1000));

        // Deposit
        CommandResult depRes = dispatcher.dispatch(s1, "bank deposit lidya 400");
        assertTrue(depRes.isSuccess());
        assertEquals(0, BigDecimal.valueOf(400).compareTo(bankService.balance(p1, CurrencyType.LIDYA)));

        // Balance check
        CommandResult balRes = dispatcher.dispatch(s1, "bank bal");
        assertTrue(balRes.isSuccess());

        // Withdraw
        CommandResult withRes = dispatcher.dispatch(s1, "bank withdraw lidya 150");
        assertTrue(withRes.isSuccess());
        assertEquals(0, BigDecimal.valueOf(250).compareTo(bankService.balance(p1, CurrencyType.LIDYA)));
    }

    @Test
    void testShopCommand() {
        UUID p1 = UUID.randomUUID();
        SimplePlayerCommandSender s1 = new SimplePlayerCommandSender(p1, "Shopper");
        s1.addPermission("mcoin.economy.shop");

        economyService.deposit(p1, CurrencyType.LIDYA, BigDecimal.valueOf(500));
        shopService.registerOffer(new ShopOffer(
            "diamond",
            CurrencyType.LIDYA,
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(50)
        ));

        // List
        CommandResult listRes = dispatcher.dispatch(s1, "shop list");
        assertTrue(listRes.isSuccess());

        // Buy
        CommandResult buyRes = dispatcher.dispatch(s1, "shop buy diamond 2");
        assertTrue(buyRes.isSuccess());
        assertEquals(0, BigDecimal.valueOf(300).compareTo(economyService.account(p1).getBalance(CurrencyType.LIDYA)));
    }

    @Test
    void testTradeCommand() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        SimplePlayerCommandSender s1 = new SimplePlayerCommandSender(p1, "Trader1");
        SimplePlayerCommandSender s2 = new SimplePlayerCommandSender(p2, "Trader2");
        s1.addPermission("mcoin.economy.trade");
        s2.addPermission("mcoin.economy.trade");

        CommandResult reqRes = dispatcher.dispatch(s1, "trade request " + p2);
        assertTrue(reqRes.isSuccess());
    }
}
