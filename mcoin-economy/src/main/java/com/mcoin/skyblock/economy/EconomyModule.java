package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.plugin.PluginRegistry;
import com.mcoin.skyblock.economy.command.BankCommand;
import com.mcoin.skyblock.economy.command.MCoinCurrencyCommand;
import com.mcoin.skyblock.economy.command.ShopCommand;
import com.mcoin.skyblock.economy.command.TradeCommand;

import java.util.Objects;

/**
 * Economy module wiring and service bundle.
 */
public final class EconomyModule {
    private final EconomyService economyService;
    private final BankService bankService;
    private final ServerShopService shopService;
    private final TradeService tradeService;

    public EconomyModule(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
        this.bankService = new BankService(economyService);
        this.shopService = new ServerShopService(economyService);
        this.tradeService = new TradeService(economyService);
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinEconomyPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new MCoinCurrencyCommand(economyService, CurrencyType.MCOIN));
            dispatcher.register(new MCoinCurrencyCommand(economyService, CurrencyType.LIDYA));
            dispatcher.register(new BankCommand(bankService));
            dispatcher.register(new ShopCommand(shopService));
            dispatcher.register(new TradeCommand(tradeService));
        }
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

    public BankService getBankService() {
        return bankService;
    }

    public ServerShopService getShopService() {
        return shopService;
    }

    public TradeService tradeService() {
        return tradeService;
    }
}
