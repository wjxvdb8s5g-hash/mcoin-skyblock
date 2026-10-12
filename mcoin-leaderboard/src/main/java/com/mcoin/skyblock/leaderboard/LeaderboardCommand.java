package com.mcoin.skyblock.leaderboard;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.currency.CurrencyType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class LeaderboardCommand implements MCoinCommand {
    private final LeaderboardService leaderboardService;

    public LeaderboardCommand(final LeaderboardService leaderboardService) {
        this.leaderboardService = Objects.requireNonNull(leaderboardService, "leaderboardService");
    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("top");
    }

    @Override
    public String getPermission() {
        return "mcoin.leaderboard.use";
    }

    @Override
    public String getDescription() {
        return "Display top island levels and economy rankings.";
    }

    @Override
    public String getUsage() {
        return "/top [islands|lidya|mcoin]";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        String category = args.length > 0 ? args[0].toLowerCase() : "islands";
        switch (category) {
            case "island":
            case "islands": {
                List<LeaderboardEntry> list = leaderboardService.getTopIslands(10);
                StringBuilder sb = new StringBuilder("&b=== &6Top Island Levels &b===\n");
                if (list.isEmpty()) {
                    sb.append("&7No islands recorded yet.");
                } else {
                    for (LeaderboardEntry entry : list) {
                        sb.append("&e#").append(entry.getRank()).append(" &f").append(entry.getLabel())
                          .append(" &7- &a").append(entry.getDisplayScore()).append("\n");
                    }
                }
                return CommandResult.success(sb.toString().trim());
            }
            case "lidya": {
                List<LeaderboardEntry> list = leaderboardService.getTopCurrency(CurrencyType.LIDYA, 10);
                StringBuilder sb = new StringBuilder("&b=== &6Top Lidya Balances &b===\n");
                if (list.isEmpty()) {
                    sb.append("&7No accounts recorded yet.");
                } else {
                    for (LeaderboardEntry entry : list) {
                        sb.append("&e#").append(entry.getRank()).append(" &f").append(entry.getLabel())
                          .append(" &7- &a").append(entry.getDisplayScore()).append("\n");
                    }
                }
                return CommandResult.success(sb.toString().trim());
            }
            case "mcoin": {
                List<LeaderboardEntry> list = leaderboardService.getTopCurrency(CurrencyType.MCOIN, 10);
                StringBuilder sb = new StringBuilder("&b=== &6Top MCOIN Balances &b===\n");
                if (list.isEmpty()) {
                    sb.append("&7No accounts recorded yet.");
                } else {
                    for (LeaderboardEntry entry : list) {
                        sb.append("&e#").append(entry.getRank()).append(" &f").append(entry.getLabel())
                          .append(" &7- &a").append(entry.getDisplayScore()).append("\n");
                    }
                }
                return CommandResult.success(sb.toString().trim());
            }
            default:
                return CommandResult.failure("Unknown category: " + category + ". Usage: /top [islands|lidya|mcoin]");
        }
    }
}
