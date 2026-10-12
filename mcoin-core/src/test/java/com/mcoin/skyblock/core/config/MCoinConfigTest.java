package com.mcoin.skyblock.core.config;

import com.mcoin.skyblock.core.storage.DatabaseMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MCoinConfigTest {

    @Test
    void testLoadDefaultsAndSave(@TempDir Path tempDir) throws Exception {
        File configFile = tempDir.resolve("mcoin.properties").toFile();
        assertFalse(configFile.exists());

        MCoinConfig config = MCoinConfig.load(configFile);
        assertNotNull(config);
        assertTrue(configFile.exists());

        // Check defaults
        DatabaseConfig db = config.getDatabase();
        assertNotNull(db);
        assertEquals(DatabaseMode.SQLITE, db.getMode());

        EconomyConfig eco = config.getEconomy();
        assertNotNull(eco);
        assertEquals(new BigDecimal("100.00"), eco.getStartingMcoin());
        assertEquals(new BigDecimal("1000.00"), eco.getStartingLidya());

        IslandConfig island = config.getIsland();
        assertNotNull(island);
        assertEquals(100, island.getDefaultSize());
        assertEquals(500, island.getMaxIslandSize());

        // Reload from saved file
        MCoinConfig reloaded = MCoinConfig.load(configFile);
        assertEquals(DatabaseMode.SQLITE, reloaded.getDatabase().getMode());
        assertEquals(new BigDecimal("100.00"), reloaded.getEconomy().getStartingMcoin());
    }
}
