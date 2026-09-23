package com.mcoin.skyblock.farming;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinFarmingPlugin extends AbstractFeaturePlugin {
    public MCoinFarmingPlugin() {
        super(PluginDescriptor.builder("MCoinFarming", "MCoin Farming")
            .description("Farm plots, crops, minions, auto-harvest and farming progression features.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.FARMING))
            .commands(Arrays.asList("farm", "crop", "minion"))
            .capabilities(Arrays.asList("farm-blocks", "crop-management", "minion-automation", "auto-harvest", "farming-skill"))
            .build());
    }
}
