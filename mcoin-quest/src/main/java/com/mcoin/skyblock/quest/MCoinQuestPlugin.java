package com.mcoin.skyblock.quest;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinQuestPlugin extends AbstractFeaturePlugin {
    public MCoinQuestPlugin() {
        super(PluginDescriptor.builder("MCoinQuest", "MCoin Quest")
            .description("Daily, weekly, mission, bounty and challenge reward systems.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.QUEST))
            .commands(Arrays.asList("quest", "daily", "weekly", "bounty", "challenge"))
            .capabilities(Arrays.asList("daily-quests", "weekly-quests", "special-missions", "bounties", "quest-rewards", "quest-leaderboards"))
            .build());
    }
}
