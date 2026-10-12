package com.mcoin.skyblock.core.bootstrap;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.ConsoleCommandSender;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.island.IslandProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MCoinSkyblockBootstrapTest {

    @Test
    void testFullBootstrapLifecycle(@TempDir Path tempDir) {
        File pluginFolder = tempDir.toFile();
        MCoinSkyblockBootstrap bootstrap = new MCoinSkyblockBootstrap(pluginFolder);

        assertFalse(bootstrap.isRunning());
        bootstrap.start();
        assertTrue(bootstrap.isRunning());

        assertNotNull(bootstrap.getConfig());
        assertNotNull(bootstrap.getStorageService());
        assertNotNull(bootstrap.getEconomyService());
        assertNotNull(bootstrap.getIslandService());
        assertNotNull(bootstrap.getPluginRegistry());
        assertNotNull(bootstrap.getCommandDispatcher());

        // Check economy integration
        UUID player1 = UUID.randomUUID();
        bootstrap.getEconomyService().deposit(player1, CurrencyType.LIDYA, BigDecimal.valueOf(1000));
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(
            bootstrap.getEconomyService().account(player1).getBalance(CurrencyType.LIDYA)
        ));

        // Create island
        IslandProfile island = bootstrap.getIslandService().createIsland(player1, "plains");
        assertNotNull(island);
        assertEquals(player1, island.getOwnerId());

        // Test Core Command execution via Dispatcher
        ConsoleCommandSender console = new ConsoleCommandSender();
        CommandResult result = bootstrap.getCommandDispatcher().dispatch(console, "mcoinadmin", new String[]{"status"});
        assertTrue(result.isSuccess());

        CommandResult reloadResult = bootstrap.getCommandDispatcher().dispatch(console, "mcoinadmin", new String[]{"reload"});
        assertTrue(reloadResult.isSuccess());

        CommandResult coreResult = bootstrap.getCommandDispatcher().dispatch(console, "mcoincore", new String[0]);
        assertTrue(coreResult.isSuccess());

        // Save data
        bootstrap.save();

        // Shutdown
        bootstrap.shutdown();
        assertFalse(bootstrap.isRunning());

        // Re-bootstrap from same folder to verify data persistence
        MCoinSkyblockBootstrap reloadedBootstrap = new MCoinSkyblockBootstrap(pluginFolder);
        reloadedBootstrap.start();
        assertTrue(reloadedBootstrap.isRunning());

        // Check account restored
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(
            reloadedBootstrap.getEconomyService().account(player1).getBalance(CurrencyType.LIDYA)
        ));

        // Check island restored
        assertTrue(reloadedBootstrap.getIslandService().findByOwner(player1).isPresent());

        reloadedBootstrap.shutdown();
    }
}
