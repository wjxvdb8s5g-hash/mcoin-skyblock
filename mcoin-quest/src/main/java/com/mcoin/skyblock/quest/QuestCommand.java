package com.mcoin.skyblock.quest;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class QuestCommand implements MCoinCommand {
    private final QuestService questService;

    public QuestCommand(final QuestService questService) {
        this.questService = Objects.requireNonNull(questService, "questService");
    }

    @Override
    public String getName() {
        return "quest";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("quests");
    }

    @Override
    public String getPermission() {
        return "mcoin.quest.use";
    }

    @Override
    public String getDescription() {
        return "View and claim daily, weekly and challenge quests.";
    }

    @Override
    public String getUsage() {
        return "/quest [list|claim <questId>]";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.failure("Only players can use /quest commands.");
        }
        final UUID playerId = sender.getUniqueId();

        if (args.length == 0 || "list".equalsIgnoreCase(args[0])) {
            StringBuilder sb = new StringBuilder("&b=== &6Skyblock Quests &b===\n");
            for (QuestDefinition qd : questService.getQuests()) {
                QuestProgress qp = questService.getProgress(playerId, qd.getId());
                String status = qp.isClaimed() ? "&8[Claimed]" : (qp.isCompleted() ? "&a[Ready to Claim!]" : "&e[" + qp.getCurrentAmount() + "/" + qd.getTargetAmount() + "]");
                sb.append("&e").append(qd.getTitle()).append(" &7(").append(qd.getCategory()).append(") ")
                  .append(status).append("\n")
                  .append("  &7").append(qd.getDescription()).append("\n")
                  .append("  &7Rewards: &a").append(qd.getRewardLidya().toPlainString()).append(" Lidya")
                  .append("&7, &e").append(qd.getRewardMcoin().toPlainString()).append(" MCOIN\n");
            }
            sb.append("\n&7Type &e/quest claim <id> &7to claim completed rewards.");
            return CommandResult.success(sb.toString().trim());
        }

        if ("claim".equalsIgnoreCase(args[0])) {
            if (args.length < 2) {
                return CommandResult.failure("Usage: /quest claim <questId>");
            }
            String questId = args[1];
            try {
                questService.claimReward(playerId, questId);
                QuestDefinition def = questService.findQuest(questId).orElse(null);
                return CommandResult.success("&aClaimed rewards for &e" + (def != null ? def.getTitle() : questId) + "&a!");
            } catch (Exception ex) {
                return CommandResult.failure(ex.getMessage());
            }
        }

        return CommandResult.failure("Unknown action: " + args[0] + ". Usage: /quest [list|claim <questId>]");
    }
}
