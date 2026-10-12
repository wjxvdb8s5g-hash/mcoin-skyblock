package com.mcoin.skyblock.progression;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RankCommand implements MCoinCommand {
    private final ProgressionService progressionService;

    public RankCommand(final ProgressionService progressionService) {
        this.progressionService = Objects.requireNonNull(progressionService, "progressionService");
    }

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ranks");
    }

    @Override
    public String getPermission() {
        return "mcoin.rank.use";
    }

    @Override
    public String getDescription() {
        return "View and purchase VIP ranks using MCOIN.";
    }

    @Override
    public String getUsage() {
        return "/rank [list|buy <rank>]";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.failure("Only players can use /rank commands.");
        }
        final UUID playerId = sender.getUniqueId();
        PlayerProgression prog = progressionService.getProgression(playerId);

        if (args.length == 0 || "list".equalsIgnoreCase(args[0])) {
            StringBuilder sb = new StringBuilder("&b=== &6Skyblock VIP Ranks &b===\n");
            sb.append("&7Your Current Rank: ").append(prog.getRank().getPrefix()).append(" ").append(prog.getRank().getDisplayName()).append("\n\n");
            for (RankType rank : RankType.values()) {
                if (rank == RankType.MEMBER) continue;
                sb.append(rank.getPrefix()).append(" ").append(rank.getDisplayName())
                  .append(" &7- Cost: &e").append(rank.getCostMcoin().toPlainString()).append(" MCOIN")
                  .append(" &7(Boost: &b").append(rank.getBoostMultiplier()).append("x&7)\n");
            }
            sb.append("\n&7Type &e/rank buy <rank> &7to upgrade your rank.");
            return CommandResult.success(sb.toString().trim());
        }

        if ("buy".equalsIgnoreCase(args[0])) {
            if (args.length < 2) {
                return CommandResult.failure("Usage: /rank buy <rank>");
            }
            String targetName = args[1].toUpperCase().replace("+", "_PLUS");
            RankType targetRank;
            try {
                targetRank = RankType.valueOf(targetName);
            } catch (IllegalArgumentException e) {
                return CommandResult.failure("Unknown rank: " + args[1] + ". Use /rank list.");
            }

            try {
                progressionService.purchaseRank(playerId, targetRank);
                return CommandResult.success("&aCongratulations! You are now " + targetRank.getPrefix() + " " + targetRank.getDisplayName() + "&a!");
            } catch (Exception ex) {
                return CommandResult.failure(ex.getMessage());
            }
        }

        return CommandResult.failure("Unknown action: " + args[0] + ". Usage: /rank [list|buy <rank>]");
    }
}
