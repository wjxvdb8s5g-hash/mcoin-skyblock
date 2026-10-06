package com.mcoin.skyblock.economy;

import com.mcoin.skyblock.core.plugin.AbstractFeaturePlugin;
import com.mcoin.skyblock.core.plugin.FeatureArea;
import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;

public final class MCoinEconomyPlugin extends AbstractFeaturePlugin {
    public MCoinEconomyPlugin() {
        super(PluginDescriptor.builder("MCoinEconomy", "MCoin Economy")
            .description("Dual-currency economy services for MCOIN, Lidya, shops, trading, auctions and banking.")
            .minecraftRange("1.8.x", "1.21.x")
            .databaseModes(EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL))
            .featureAreas(EnumSet.of(FeatureArea.ECONOMY))
            .commands(Arrays.asList("mcoin", "lidya", "shop", "trade", "auction", "bank", "market"))
            .capabilities(Arrays.asList("premium-currency", "in-game-currency", "npc-shop", "player-trading", "auction-house", "bank-vaults", "player-market"))
            .build());
    }
}
