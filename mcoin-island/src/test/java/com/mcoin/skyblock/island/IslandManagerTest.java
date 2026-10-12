package com.mcoin.skyblock.island;

import com.mcoin.skyblock.core.config.IslandConfig;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IslandManagerTest {

    private IslandService islandService;
    private EconomyService economyService;
    private IslandConfig islandConfig;
    private IslandManager islandManager;

    @BeforeEach
    void setUp() {
        islandService = new IslandService();
        economyService = new EconomyService();
        islandConfig = IslandConfig.defaultConfig();
        islandManager = new IslandManager(islandService, economyService, islandConfig);
    }

    @Test
    void testCreateIslandSuccess() {
        UUID player = UUID.randomUUID();
        IslandProfile island = islandManager.createIsland(player, "PLAINS");
        assertNotNull(island);
        assertEquals(player, island.getOwnerId());
        assertEquals("PLAINS", island.getBiome());
        assertEquals(islandConfig.getDefaultSize(), island.getSize());
        assertEquals(1, island.getLevel());

        assertTrue(islandService.findByOwner(player).isPresent());
    }

    @Test
    void testUpgradeAndExpandIsland() {
        UUID player = UUID.randomUUID();
        economyService.deposit(player, CurrencyType.LIDYA, BigDecimal.valueOf(10000));

        IslandProfile island = islandManager.createIsland(player, "PLAINS");

        // Upgrade
        islandManager.upgradeLevel(player, 2);
        IslandProfile upgraded = islandService.findByIslandId(island.getIslandId()).get();
        assertEquals(3, upgraded.getLevel());

        // Expand
        islandManager.expandIsland(player, 20);
        IslandProfile expanded = islandService.findByIslandId(island.getIslandId()).get();
        assertEquals(islandConfig.getDefaultSize() + 20, expanded.getSize());

        // Biome change
        islandManager.changeBiome(player, "DESERT");
        assertEquals("DESERT", islandService.findByIslandId(island.getIslandId()).get().getBiome());
    }
}
