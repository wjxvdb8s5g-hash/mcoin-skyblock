package com.mcoin.skyblock.island;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.SimplePlayerCommandSender;
import com.mcoin.skyblock.core.config.IslandConfig;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IslandCommandTest {

    private IslandService islandService;
    private EconomyService economyService;
    private IslandConfig islandConfig;
    private IslandManager islandManager;
    private IslandCommand islandCommand;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        islandService = new IslandService();
        economyService = new EconomyService();
        islandConfig = IslandConfig.defaultConfig();
        islandManager = new IslandManager(islandService, economyService, islandConfig);
        islandCommand = new IslandCommand(islandManager);
        dispatcher = new CommandDispatcher();
        dispatcher.register(islandCommand);
    }

    @Test
    void testIslandCreateAndInfoCommand() {
        UUID playerId = UUID.randomUUID();
        SimplePlayerCommandSender player = new SimplePlayerCommandSender(playerId, "SkyblockPlayer");
        player.addPermission("mcoin.island.use");

        // Fund player
        economyService.deposit(playerId, CurrencyType.LIDYA, BigDecimal.valueOf(5000));

        // Create island via command
        CommandResult createRes = dispatcher.dispatch(player, "is create plains");
        assertTrue(createRes.isSuccess());

        // Info command
        CommandResult infoRes = dispatcher.dispatch(player, "is info");
        assertTrue(infoRes.isSuccess());

        // Upgrade command
        CommandResult upRes = dispatcher.dispatch(player, "is upgrade");
        assertTrue(upRes.isSuccess());

        // Expand command
        CommandResult expRes = dispatcher.dispatch(player, "is expand 10");
        assertTrue(expRes.isSuccess());

        // Biome command
        CommandResult bioRes = dispatcher.dispatch(player, "is biome desert");
        assertTrue(bioRes.isSuccess());
    }
}
