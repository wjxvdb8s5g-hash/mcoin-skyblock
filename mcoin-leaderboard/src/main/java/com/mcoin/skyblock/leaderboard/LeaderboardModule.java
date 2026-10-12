package com.mcoin.skyblock.leaderboard;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandService;
import com.mcoin.skyblock.core.plugin.PluginRegistry;

import java.util.Objects;

/**
 * Leaderboard module wiring and service bundle.
 */
public final class LeaderboardModule {
    private final LeaderboardService leaderboardService;

    public LeaderboardModule(final IslandService islandService, final EconomyService economyService) {
        this.leaderboardService = new LeaderboardService(
            Objects.requireNonNull(islandService, "islandService"),
            Objects.requireNonNull(economyService, "economyService")
        );
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinLeaderboardPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new LeaderboardCommand(leaderboardService));
        }
    }

    public LeaderboardService getLeaderboardService() {
        return leaderboardService;
    }
}
