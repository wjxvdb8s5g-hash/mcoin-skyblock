package com.mcoin.skyblock.progression;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class SkillsCommand implements MCoinCommand {
    private final ProgressionService progressionService;

    public SkillsCommand(final ProgressionService progressionService) {
        this.progressionService = Objects.requireNonNull(progressionService, "progressionService");
    }

    @Override
    public String getName() {
        return "skills";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("skill");
    }

    @Override
    public String getPermission() {
        return "mcoin.skills.use";
    }

    @Override
    public String getDescription() {
        return "View your Skyblock skill levels and XP progress.";
    }

    @Override
    public String getUsage() {
        return "/skills";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.failure("Only players can check skills.");
        }
        final UUID playerId = sender.getUniqueId();
        PlayerProgression prog = progressionService.getProgression(playerId);

        StringBuilder sb = new StringBuilder("&b=== &6Skyblock Skills &7(Prestige: &e")
            .append(prog.getPrestige()).append("&7) &b===\n");
        sb.append("&7Rank: ").append(prog.getRank().getPrefix()).append(" ").append(prog.getRank().getDisplayName())
          .append(" &7(Multiplier: &e").append(prog.getRank().getBoostMultiplier()).append("x)\n")
          .append("&7Total Skill Level: &e").append(prog.getTotalSkillLevel()).append("\n\n");

        for (SkillType skill : SkillType.values()) {
            int lvl = prog.getLevel(skill);
            double xp = prog.getXp(skill);
            double needed = PlayerProgression.xpForLevel(lvl + 1);
            sb.append("&a").append(skill.getDisplayName()).append(" &7[Lvl &e").append(lvl).append("&7]: ")
              .append("&f").append(String.format("%.1f", xp)).append("/").append(String.format("%.1f", needed)).append(" XP\n");
        }

        return CommandResult.success(sb.toString().trim());
    }
}
