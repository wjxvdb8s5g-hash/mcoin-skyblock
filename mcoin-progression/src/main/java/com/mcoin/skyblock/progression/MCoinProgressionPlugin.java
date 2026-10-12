package com.mcoin.skyblock.progression;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinProgressionPlugin extends AbstractFeaturePlugin {
    public MCoinProgressionPlugin() {
        super(PluginDescriptor.builder("MCoinProgression", "MCoin Progression")
            .description("VIP ranks, prestige, skill, mastery, achievement and experience progression.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.PROGRESSION))
            .commands(Arrays.asList("rank", "prestige", "skills", "mastery", "achievements", "xp"))
            .capabilities(Arrays.asList("vip-ranks", "prestige-system", "skill-leveling", "mastery-tracks", "achievement-tracking", "experience-leveling"))
            .build());
    }
}
