package com.mcoin.skyblock.core.currency;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class EconomyService {
    private final Map<UUID, CurrencyAccount> accounts = new LinkedHashMap<UUID, CurrencyAccount>();

    public CurrencyAccount account(final UUID playerId) {
        final UUID safePlayerId = Objects.requireNonNull(playerId, "playerId");
        CurrencyAccount account = accounts.get(safePlayerId);
        if (account == null) {
            account = new CurrencyAccount(safePlayerId);
            accounts.put(safePlayerId, account);
        }
        return account;
    }

    public void deposit(final UUID playerId, final CurrencyType type, final BigDecimal amount) {
        account(playerId).deposit(type, amount);
    }

    public void withdraw(final UUID playerId, final CurrencyType type, final BigDecimal amount) {
        account(playerId).withdraw(type, amount);
    }

    public void transfer(final UUID fromPlayerId, final UUID toPlayerId, final CurrencyType type, final BigDecimal amount) {
        withdraw(fromPlayerId, type, amount);
        deposit(toPlayerId, type, amount);
    }

    public Map<UUID, CurrencyAccount> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<UUID, CurrencyAccount>(accounts));
    }
}
