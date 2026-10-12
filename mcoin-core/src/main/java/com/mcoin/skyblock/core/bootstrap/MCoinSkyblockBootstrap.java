package com.mcoin.skyblock.core.bootstrap;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinAdminCommand;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.command.MCoinCoreCommand;
import com.mcoin.skyblock.core.config.MCoinConfig;
import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;
import com.mcoin.skyblock.core.plugin.FeaturePlugin;
import com.mcoin.skyblock.core.plugin.MCoinCorePlugin;
import com.mcoin.skyblock.core.plugin.PluginRegistry;
import com.mcoin.skyblock.core.storage.StorageFactory;
import com.mcoin.skyblock.core.storage.StorageService;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Central orchestrator and bootstrap for the MCoin Skyblock plugin suite.
 * Manages configuration, storage lifecycle, core services, command routing, and module registries.
 */
public final class MCoinSkyblockBootstrap {
    private static final Logger LOGGER = Logger.getLogger(MCoinSkyblockBootstrap.class.getName());

    private final File dataFolder;
    private MCoinConfig config;
    private StorageService storageService;
    private final EconomyService economyService;
    private final IslandService islandService;
    private final CommandDispatcher commandDispatcher;
    private final PluginRegistry pluginRegistry;
    private boolean running;

    public MCoinSkyblockBootstrap() {
        this(null);
    }

    public MCoinSkyblockBootstrap(final File dataFolder) {
        this.dataFolder = dataFolder;
        this.config = MCoinConfig.defaultConfig();
        this.economyService = new EconomyService();
        this.islandService = new IslandService();
        this.commandDispatcher = new CommandDispatcher();
        this.pluginRegistry = new PluginRegistry();
        this.running = false;
    }

    public synchronized void start() {
        if (running) {
            throw new IllegalStateException("MCoin Skyblock Bootstrap is already running.");
        }

        LOGGER.info("[MCoin-Skyblock] Bootstrapping MCoin Skyblock package...");

        // 1. Load configuration
        if (dataFolder != null) {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File configFile = new File(dataFolder, "config.properties");
            if (configFile.exists()) {
                try {
                    this.config = MCoinConfig.load(configFile);
                    LOGGER.info("[MCoin-Skyblock] Configuration loaded from " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    LOGGER.warning("[MCoin-Skyblock] Failed to read config file, using defaults: " + e.getMessage());
                }
            } else {
                try {
                    this.config.save(configFile);
                    LOGGER.info("[MCoin-Skyblock] Default configuration generated at " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    LOGGER.warning("[MCoin-Skyblock] Failed to save default configuration: " + e.getMessage());
                }
            }
        }

        // 2. Initialize storage
        this.storageService = StorageFactory.create(this.config.getDatabaseConfig(), dataFolder);
        this.storageService.init();
        LOGGER.info("[MCoin-Skyblock] Initialized storage engine in " + this.config.getDatabaseConfig().getMode() + " mode.");

        // 3. Load persistent data into memory
        Map<UUID, CurrencyAccount> accounts = storageService.loadAllAccounts();
        for (CurrencyAccount acc : accounts.values()) {
            CurrencyAccount target = economyService.account(acc.getPlayerId());
            for (CurrencyType type : CurrencyType.values()) {
                target.setBalance(type, acc.getBalance(type));
            }
        }
        LOGGER.info("[MCoin-Skyblock] Loaded " + accounts.size() + " currency accounts from storage.");

        Map<UUID, IslandProfile> islands = storageService.loadAllIslands();
        for (IslandProfile island : islands.values()) {
            islandService.registerIsland(island);
        }
        LOGGER.info("[MCoin-Skyblock] Loaded " + islands.size() + " islands from storage.");

        // 4. Register core plugin descriptors
        pluginRegistry.register(new MCoinCorePlugin());

        // 5. Register core commands
        commandDispatcher.register(new MCoinCoreCommand(pluginRegistry));
        commandDispatcher.register(new MCoinAdminCommand(economyService, storageService, this::reload));

        this.running = true;
        LOGGER.info("[MCoin-Skyblock] MCoin Skyblock package initialized successfully.");
    }

    public synchronized void save() {
        if (!running || storageService == null) {
            return;
        }
        LOGGER.info("[MCoin-Skyblock] Persisting accounts and islands to storage...");
        for (CurrencyAccount acc : economyService.snapshot().values()) {
            storageService.saveAccount(acc);
        }
        for (IslandProfile island : islandService.snapshot().values()) {
            storageService.saveIsland(island);
        }
        LOGGER.info("[MCoin-Skyblock] Data persisted successfully.");
    }

    public synchronized void reload() {
        if (!running) {
            return;
        }
        save();
        if (dataFolder != null) {
            File configFile = new File(dataFolder, "config.properties");
            if (configFile.exists()) {
                try {
                    this.config = MCoinConfig.load(configFile);
                    LOGGER.info("[MCoin-Skyblock] Configuration reloaded.");
                } catch (IOException e) {
                    LOGGER.warning("[MCoin-Skyblock] Could not reload configuration: " + e.getMessage());
                }
            }
        }
    }

    public synchronized void shutdown() {
        if (!running) {
            return;
        }
        LOGGER.info("[MCoin-Skyblock] Shutting down MCoin Skyblock package...");
        save();
        if (storageService != null) {
            storageService.close();
        }
        this.running = false;
        LOGGER.info("[MCoin-Skyblock] Shutdown complete.");
    }

    public void registerCommand(final MCoinCommand command) {
        commandDispatcher.register(command);
    }

    public void registerPlugin(final FeaturePlugin plugin) {
        pluginRegistry.register(plugin);
    }

    public CommandResult dispatchCommand(final CommandSender sender, final String commandLine) {
        return commandDispatcher.dispatch(sender, commandLine);
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public MCoinConfig getConfig() {
        return config;
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

    public IslandService getIslandService() {
        return islandService;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }

    public boolean isRunning() {
        return running;
    }
}
