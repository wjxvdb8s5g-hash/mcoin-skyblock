package com.mcoin.skyblock.progression;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.plugin.PluginRegistry;

import java.util.Objects;

/**
 * Progression module wiring and service bundle.
 */
public final class ProgressionModule {
    private final ProgressionService progressionService;

    public ProgressionModule(final EconomyService economyService) {
        this.progressionService = new ProgressionService(Objects.requireNonNull(economyService, "economyService"));
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinProgressionPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new SkillsCommand(progressionService));
            dispatcher.register(new RankCommand(progressionService));
        }
    }

    public ProgressionService getProgressionService() {
        return progressionService;
    }
}
