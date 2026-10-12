package com.mcoin.skyblock.utility;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class WarpCommand implements MCoinCommand {
    private final WarpService warpService;

    public WarpCommand(final WarpService warpService) {
        this.warpService = Objects.requireNonNull(warpService, "warpService");
    }

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("warps");
    }

    @Override
    public String getPermission() {
        return "mcoin.warp.use";
    }

    @Override
    public String getDescription() {
        return "Teleport to server warps.";
    }

    @Override
    public String getUsage() {
        return "/warp [list|<name>]";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (args.length == 0 || "list".equalsIgnoreCase(args[0])) {
            StringBuilder sb = new StringBuilder("&b=== &6Available Warps &b===\n");
            for (WarpPoint wp : warpService.listWarps()) {
                sb.append("&e").append(wp.getName())
                  .append(" &7(&f").append(wp.getWorld()).append("&7: ")
                  .append((int) wp.getX()).append(", ").append((int) wp.getY()).append(", ").append((int) wp.getZ())
                  .append(")\n");
            }
            sb.append("&7Use &e/warp <name> &7to teleport.");
            return CommandResult.success(sb.toString().trim());
        }

        String name = args[0].toLowerCase();
        WarpPoint warp = warpService.getWarp(name).orElse(null);
        if (warp == null) {
            return CommandResult.failure("Unknown warp: " + name + ". Use /warp list.");
        }

        if (warp.getPermission() != null && !sender.hasPermission(warp.getPermission())) {
            return CommandResult.noPermission("You do not have permission to warp to " + name + ".");
        }

        return CommandResult.success("&aTeleported to &e" + warp.getName() + "&a!");
    }
}
