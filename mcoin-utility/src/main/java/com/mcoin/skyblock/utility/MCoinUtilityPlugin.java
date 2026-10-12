package com.mcoin.skyblock.utility;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinUtilityPlugin extends AbstractFeaturePlugin {
    public MCoinUtilityPlugin() {
        super(PluginDescriptor.builder("MCoinUtility", "MCoin Utility")
            .description("Homes, warps, region safety, cleanup and admin operation services.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.UTILITY))
            .commands(Arrays.asList("home", "warp", "region", "cleanup", "mcoinadmin"))
            .capabilities(Arrays.asList("home-system", "warp-system", "region-protection", "lag-prevention", "admin-controls"))
            .build());
    }
}
