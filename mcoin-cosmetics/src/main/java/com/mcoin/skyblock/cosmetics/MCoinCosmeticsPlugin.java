package com.mcoin.skyblock.cosmetics;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinCosmeticsPlugin extends AbstractFeaturePlugin {
    public MCoinCosmeticsPlugin() {
        super(PluginDescriptor.builder("MCoinCosmetics", "MCoin Cosmetics")
            .description("Particles, emotes, pet cosmetics and visual customization hooks.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.COSMETICS))
            .commands(Arrays.asList("cosmetics", "particles", "emotes"))
            .capabilities(Arrays.asList("particle-effects", "pet-cosmetics", "emotes", "visual-customization"))
            .build());
    }
}
