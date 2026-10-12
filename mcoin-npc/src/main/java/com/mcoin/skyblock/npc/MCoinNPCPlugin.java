package com.mcoin.skyblock.npc;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinNPCPlugin extends AbstractFeaturePlugin {
    public MCoinNPCPlugin() {
        super(PluginDescriptor.builder("MCoinNPC", "MCoin NPC")
            .description("NPC management, shop, dialogue and quest giver interactions.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.NPC))
            .commands(Arrays.asList("npc", "dialogue"))
            .capabilities(Arrays.asList("npc-creation", "npc-shops", "dialogue-system", "quest-givers"))
            .build());
    }
}
