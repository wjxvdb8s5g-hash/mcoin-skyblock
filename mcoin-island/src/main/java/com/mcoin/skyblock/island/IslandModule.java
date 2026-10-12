package com.mcoin.skyblock.island;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.config.IslandConfig;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandService;
import com.mcoin.skyblock.core.plugin.PluginRegistry;

import java.util.Objects;

/**
 * Island module wiring and service bundle.
 */
public final class IslandModule {
    private final IslandService islandService;
    private final IslandManager islandManager;

    public IslandModule(final IslandService islandService,
                        final EconomyService economyService,
                        final IslandConfig islandConfig) {
        this.islandService = Objects.requireNonNull(islandService, "islandService");
        this.islandManager = new IslandManager(islandService, economyService, islandConfig);
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinIslandPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new IslandCommand(islandManager));
        }
    }

    public IslandService getIslandService() {
        return islandService;
    }

    public IslandManager getIslandManager() {
        return islandManager;
    }
}
