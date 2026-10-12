package com.mcoin.skyblock.core.storage;

import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.island.IslandProfile;
import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StorageService extends Closeable {
    void init();

    void saveAccount(CurrencyAccount account);
    Optional<CurrencyAccount> loadAccount(UUID playerId);
    Map<UUID, CurrencyAccount> loadAllAccounts();
    void deleteAccount(UUID playerId);

    void saveBankAccount(CurrencyAccount account);
    Optional<CurrencyAccount> loadBankAccount(UUID playerId);
    Map<UUID, CurrencyAccount> loadAllBankAccounts();

    void saveIsland(IslandProfile island);
    Optional<IslandProfile> loadIsland(UUID islandId);
    Optional<IslandProfile> loadIslandByOwner(UUID ownerId);
    Map<UUID, IslandProfile> loadAllIslands();
    void deleteIsland(UUID islandId);

    void flush();

    @Override
    void close();
}
