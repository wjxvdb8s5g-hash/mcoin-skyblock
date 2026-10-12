package com.mcoin.skyblock.quest;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.plugin.PluginRegistry;

import java.util.Objects;

/**
 * Quest module wiring and service bundle.
 */
public final class QuestModule {
    private final QuestService questService;

    public QuestModule(final EconomyService economyService) {
        this.questService = new QuestService(Objects.requireNonNull(economyService, "economyService"));
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinQuestPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new QuestCommand(questService));
        }
    }

    public QuestService getQuestService() {
        return questService;
    }
}
