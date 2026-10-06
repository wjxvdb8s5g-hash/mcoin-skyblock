package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class BankService {
    private final EconomyService economyService;
    private final Map<UUID, CurrencyAccount> bankAccounts = new LinkedHashMap<UUID, CurrencyAccount>();

    public BankService(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
    }

    public synchronized void deposit(final UUID playerId, final CurrencyType currencyType, final BigDecimal amount) {
        economyService.withdraw(playerId, currencyType, amount);
        bankAccount(playerId).deposit(currencyType, amount);
    }

    public synchronized void withdraw(final UUID playerId, final CurrencyType currencyType, final BigDecimal amount) {
        bankAccount(playerId).withdraw(currencyType, amount);
        economyService.deposit(playerId, currencyType, amount);
    }

    public synchronized BigDecimal balance(final UUID playerId, final CurrencyType currencyType) {
        return bankAccount(playerId).getBalance(currencyType);
    }

    public synchronized Map<UUID, CurrencyAccount> snapshot() {
        Map<UUID, CurrencyAccount> copy = new LinkedHashMap<UUID, CurrencyAccount>(bankAccounts.size());
        for (Map.Entry<UUID, CurrencyAccount> entry : bankAccounts.entrySet()) {
            copy.put(entry.getKey(), CurrencyAccount.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    private CurrencyAccount bankAccount(final UUID playerId) {
        CurrencyAccount account = bankAccounts.get(Objects.requireNonNull(playerId, "playerId"));
        if (account == null) {
            account = new CurrencyAccount(playerId);
            bankAccounts.put(playerId, account);
        }
        return account;
    }
}
