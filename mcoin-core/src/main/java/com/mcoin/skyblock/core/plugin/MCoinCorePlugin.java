package com.mcoin.skyblock.core.plugin;

import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinCorePlugin extends AbstractFeaturePlugin {
    public MCoinCorePlugin() {
        super(PluginDescriptor.builder("MCoinCore", "MCoin Core")
            .description("Shared APIs, configuration, database mode support and admin orchestration.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.CORE, FeatureArea.UTILITY))
            .commands(Arrays.asList("mcoincore", "mcoinadmin"))
            .capabilities(Arrays.asList("shared-api", "configuration", "database-support", "module-registry", "admin-panel-hooks"))
            .build());
    }
}
