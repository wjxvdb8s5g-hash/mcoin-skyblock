package com.mcoin.skyblock.core.storage;

import com.mcoin.skyblock.core.config.DatabaseConfig;
import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.island.IslandProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    @Test
    void testSqlStorageServiceLifecycleAndCrud() {
        DatabaseConfig dbConfig = new DatabaseConfig(
            DatabaseMode.SQLITE,
            ":memory:",
            "localhost",
            3306,
            "testdb",
            "user",
            "pass",
            5
        );

        SqlStorageService storage = new SqlStorageService(dbConfig);
        storage.init();

        UUID playerId = UUID.randomUUID();
        CurrencyAccount account = new CurrencyAccount(playerId);
        account.setBalance(CurrencyType.MCOIN, BigDecimal.valueOf(150.50));
        account.setBalance(CurrencyType.LIDYA, BigDecimal.valueOf(25000));

        storage.saveAccount(account);
        Optional<CurrencyAccount> loadedOpt = storage.loadAccount(playerId);
        assertTrue(loadedOpt.isPresent());
        CurrencyAccount loadedAccount = loadedOpt.get();
        assertEquals(playerId, loadedAccount.getPlayerId());
        assertEquals(0, new BigDecimal("150.50").compareTo(loadedAccount.getBalance(CurrencyType.MCOIN)));
        assertEquals(0, new BigDecimal("25000").compareTo(loadedAccount.getBalance(CurrencyType.LIDYA)));

        Map<UUID, CurrencyAccount> allAccounts = storage.loadAllAccounts();
        assertEquals(1, allAccounts.size());

        // Islands
        UUID islandId = UUID.randomUUID();
        IslandProfile island = new IslandProfile(islandId, playerId, "plains");
        island.setLevel(5);
        island.setSize(120);
        storage.saveIsland(island);

        Map<UUID, IslandProfile> islands = storage.loadAllIslands();
        assertEquals(1, islands.size());
        IslandProfile loadedIsland = islands.get(islandId);
        assertNotNull(loadedIsland);
        assertEquals(islandId, loadedIsland.getIslandId());
        assertEquals(playerId, loadedIsland.getOwnerId());
        assertEquals(5, loadedIsland.getLevel());
        assertEquals(120, loadedIsland.getSize());
        assertEquals("plains", loadedIsland.getBiome());

        storage.deleteIsland(islandId);
        assertTrue(storage.loadAllIslands().isEmpty());

        storage.close();
    }

    @Test
    void testFileStorageServiceLifecycleAndCrud(@TempDir Path tempDir) {
        File dataFolder = tempDir.toFile();
        FileStorageService storage = new FileStorageService(dataFolder);
        storage.init();

        UUID playerId = UUID.randomUUID();
        CurrencyAccount account = new CurrencyAccount(playerId);
        account.setBalance(CurrencyType.LIDYA, BigDecimal.valueOf(777));
        storage.saveAccount(account);

        Optional<CurrencyAccount> loadedOpt = storage.loadAccount(playerId);
        assertTrue(loadedOpt.isPresent());
        assertEquals(0, new BigDecimal("777").compareTo(loadedOpt.get().getBalance(CurrencyType.LIDYA)));

        UUID islandId = UUID.randomUUID();
        IslandProfile island = new IslandProfile(islandId, playerId, "desert");
        island.setLevel(2);
        storage.saveIsland(island);

        Map<UUID, IslandProfile> islands = storage.loadAllIslands();
        assertEquals(1, islands.size());
        assertEquals("desert", islands.get(islandId).getBiome());

        storage.deleteIsland(islandId);
        assertTrue(storage.loadAllIslands().isEmpty());

        storage.close();
    }

    @Test
    void testStorageFactoryFallback(@TempDir Path tempDir) {
        StorageService storage = StorageFactory.fileBased(tempDir.toFile());
        assertNotNull(storage);
        assertTrue(storage instanceof FileStorageService);
        storage.close();
    }
}
