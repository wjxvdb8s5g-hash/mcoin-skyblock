package com.mcoin.skyblock.leaderboard;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinLeaderboardPlugin extends AbstractFeaturePlugin {
    public MCoinLeaderboardPlugin() {
        super(PluginDescriptor.builder("MCoinLeaderboard", "MCoin Leaderboard")
            .description("Island ranking, seasonal ladders and player statistics tracking.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.LEADERBOARD))
            .commands(Arrays.asList("leaderboard", "top", "stats"))
            .capabilities(Arrays.asList("island-rankings", "player-statistics", "seasonal-rankings", "achievement-progress"))
            .build());
    }
}
