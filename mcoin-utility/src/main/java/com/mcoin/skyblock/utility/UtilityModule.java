package com.mcoin.skyblock.utility;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.plugin.PluginRegistry;

/**
 * Utility module wiring and service bundle.
 */
public final class UtilityModule {
    private final WarpService warpService;

    public UtilityModule() {
        this.warpService = new WarpService();
    }

    public void register(final PluginRegistry registry, final CommandDispatcher dispatcher) {
        if (registry != null) {
            registry.register(new MCoinUtilityPlugin());
        }
        if (dispatcher != null) {
            dispatcher.register(new WarpCommand(warpService));
        }
    }

    public WarpService getWarpService() {
        return warpService;
    }
}
