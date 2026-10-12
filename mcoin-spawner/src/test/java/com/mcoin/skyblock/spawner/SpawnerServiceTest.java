package com.mcoin.skyblock.spawner;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.SimplePlayerCommandSender;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SpawnerServiceTest {

    private EconomyService economyService;
    private IslandService islandService;
    private SpawnerService spawnerService;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        economyService = new EconomyService();
        islandService = new IslandService();
        spawnerService = new SpawnerService(economyService);
        dispatcher = new CommandDispatcher();
        dispatcher.register(new SpawnerCommand(spawnerService, islandService));
    }

    @Test
    void testPurchaseAndUpgrade() {
        UUID player = UUID.randomUUID();
        UUID island = UUID.randomUUID();
        economyService.deposit(player, CurrencyType.LIDYA, new BigDecimal("1000000"));

        SpawnerInstance spawner = spawnerService.purchaseSpawner(player, island, SpawnerType.PIG, 1);
        assertNotNull(spawner);
        assertEquals(SpawnerType.PIG, spawner.getType());
        assertEquals(1, spawner.getTier());
        assertEquals(1, spawner.getStackCount());

        spawnerService.upgradeTier(player, spawner.getSpawnerId());
        assertEquals(2, spawner.getTier());

        spawnerService.stackSpawner(player, spawner.getSpawnerId(), 4);
        assertEquals(5, spawner.getStackCount());
        assertEquals(1, spawnerService.getSpawnersForIsland(island).size());
    }

    @Test
    void testSpawnerCommand() {
        UUID player = UUID.randomUUID();
        economyService.deposit(player, CurrencyType.LIDYA, new BigDecimal("5000000"));
        IslandProfile island = islandService.createIsland(player, "SkyHome");

        SimplePlayerCommandSender sender = new SimplePlayerCommandSender(player, "SpawnerMaster");
        sender.addPermission("mcoin.spawner.use");

        CommandResult listRes = dispatcher.dispatch(sender, "spawner list");
        assertTrue(listRes.isSuccess());

        CommandResult buyRes = dispatcher.dispatch(sender, "spawner buy PIG");
        assertTrue(buyRes.isSuccess());

        SpawnerInstance instance = spawnerService.getSpawnersForIsland(island.getIslandId()).get(0);
        assertNotNull(instance);

        CommandResult upgradeRes = dispatcher.dispatch(sender, "spawner upgrade " + instance.getSpawnerId());
        assertTrue(upgradeRes.isSuccess());
        assertEquals(2, instance.getTier());

        CommandResult stackRes = dispatcher.dispatch(sender, "spawner stack " + instance.getSpawnerId() + " 3");
        assertTrue(stackRes.isSuccess());
        assertEquals(4, instance.getStackCount());

        CommandResult infoRes = dispatcher.dispatch(sender, "spawner info " + instance.getSpawnerId());
        assertTrue(infoRes.isSuccess());
    }
}
