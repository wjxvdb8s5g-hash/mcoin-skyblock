package com.mcoin.skyblock.events;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinEventsPlugin extends AbstractFeaturePlugin {
    public MCoinEventsPlugin() {
        super(PluginDescriptor.builder("MCoinEvents", "MCoin Events")
            .description("Seasonal events, holiday content and boss battle scheduling.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.EVENTS))
            .commands(Arrays.asList("event", "boss"))
            .capabilities(Arrays.asList("event-scheduling", "seasonal-events", "holiday-events", "boss-battles"))
            .build());
    }
}
