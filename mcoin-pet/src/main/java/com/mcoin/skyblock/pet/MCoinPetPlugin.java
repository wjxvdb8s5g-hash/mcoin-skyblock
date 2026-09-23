package com.mcoin.skyblock.pet;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinPetPlugin extends AbstractFeaturePlugin {
    public MCoinPetPlugin() {
        super(PluginDescriptor.builder("MCoinPet", "MCoin Pet")
            .description("Pet, companion, leveling, skills and pet marketplace systems.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.PET))
            .commands(Arrays.asList("pet", "companion", "pet market"))
            .capabilities(Arrays.asList("pet-types", "pet-leveling", "pet-skills", "companions", "pet-marketplace"))
            .build());
    }
}
