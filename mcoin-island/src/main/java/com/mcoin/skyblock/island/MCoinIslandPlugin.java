package com.mcoin.skyblock.island;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinIslandPlugin extends AbstractFeaturePlugin {
    public MCoinIslandPlugin() {
        super(PluginDescriptor.builder("MCoinIsland", "MCoin Island")
            .description("Island creation, co-op, biome, home and upgrade management services.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.ISLAND))
            .commands(Arrays.asList("island", "is home", "is biome", "is coop", "is upgrade"))
            .capabilities(Arrays.asList("island-creation", "island-expansion", "island-levels", "coop-membership", "biome-switching", "home-teleport-state"))
            .build());
    }
}
