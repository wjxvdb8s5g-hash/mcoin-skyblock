package com.mcoin.skyblock.spawner;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinSpawnerPlugin extends AbstractFeaturePlugin {
    public MCoinSpawnerPlugin() {
        super(PluginDescriptor.builder("MCoinSpawner", "MCoin Spawner")
            .description("Spawner catalog, stacking, upgrades, eggs and mob drop configuration.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.SPAWNER))
            .commands(Arrays.asList("spawner", "spawner shop", "spawner upgrade"))
            .capabilities(Arrays.asList("spawner-types-22+", "spawner-stacking", "mob-drops", "spawner-upgrades", "spawner-eggs"))
            .build());
    }
}
