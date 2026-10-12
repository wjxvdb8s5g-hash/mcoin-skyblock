package com.mcoin.skyblock.spawner;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandService;
import com.mcoin.skyblock.core.plugin.PluginRegistry;

import java.util.Objects;

/**
 * Spawner module wiring and service bundle.
 */
public final class SpawnerModule {
    private final SpawnerService spawnerService;
    private final IslandService islandService;

    public SpawnerModule(final EconomyService economyService, final IslandService islandService) {
        this.spawnerService = new SpawnerService(Objects.requireNonNull(economyService, "economyService"));
        this.islandService = Objects.requireNonNull(islandService, "islandService");
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinSpawnerPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new SpawnerCommand(spawnerService, islandService));
        }
    }

    public SpawnerService getSpawnerService() {
        return spawnerService;
    }
}
