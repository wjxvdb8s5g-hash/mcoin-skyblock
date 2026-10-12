package com.mcoin.skyblock.island;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.island.IslandMemberRole;
import com.mcoin.skyblock.core.island.IslandProfile;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class IslandCommand implements MCoinCommand {
    private final IslandManager islandManager;

    public IslandCommand(final IslandManager islandManager) {
        this.islandManager = Objects.requireNonNull(islandManager, "islandManager");
    }

    @Override
    public String getName() {
        return "island";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("is", "ada");
    }

    @Override
    public String getPermission() {
        return "mcoin.island.use";
    }

    @Override
    public String getDescription() {
        return "Main command for Skyblock island management, upgrades, and co-op.";
    }

    @Override
    public String getUsage() {
        return "/island <create|home|sethome|biome|upgrade|expand|coop|info>";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.notAPlayer();
        }

        UUID playerId = sender.getUniqueId();

        if (args == null || args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            sender.sendMessage("&6=== &eSkyblock Island Commands &6===");
            sender.sendMessage("&e/is create [biome] &7- Create a new island");
            sender.sendMessage("&e/is home &7- Teleport to your island home");
            sender.sendMessage("&e/is sethome <warp> &7- Set your island home warp");
            sender.sendMessage("&e/is biome <biome> &7- Change island biome");
            sender.sendMessage("&e/is upgrade [levels] &7- Upgrade island level with Lidya");
            sender.sendMessage("&e/is expand <blocks> &7- Expand island border with Lidya");
            sender.sendMessage("&e/is coop <add|remove> <uuid> &7- Manage co-op members");
            sender.sendMessage("&e/is info &7- View island information");
            return CommandResult.success();
        }

        String sub = args[0].toLowerCase();
        try {
            switch (sub) {
                case "create": {
                    String biome = args.length > 1 ? args[1] : null;
                    IslandProfile profile = islandManager.createIsland(playerId, biome);
                    return CommandResult.success("&a[Island] Successfully created your island! Biome: &e" + profile.getBiome());
                }
                case "home": {
                    String warp = islandManager.getHomeWarp(playerId);
                    return CommandResult.success("&a[Island] Teleporting to island home warp: &e" + warp);
                }
                case "sethome": {
                    String warp = args.length > 1 ? args[1] : "island-home";
                    islandManager.setHomeWarp(playerId, warp);
                    return CommandResult.success("&a[Island] Island home set to: &e" + warp);
                }
                case "biome": {
                    if (args.length < 2) {
                        return CommandResult.usage("/is biome <biome_name>");
                    }
                    String newBiome = args[1];
                    islandManager.changeBiome(playerId, newBiome);
                    return CommandResult.success("&a[Island] Biome changed to &e" + newBiome.toUpperCase() + " &a(Cost deducted).");
                }
                case "upgrade": {
                    int levels = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                    islandManager.upgradeLevel(playerId, levels);
                    return CommandResult.success("&a[Island] Island upgraded by &e" + levels + " &alevel(s)!");
                }
                case "expand": {
                    if (args.length < 2) {
                        return CommandResult.usage("/is expand <blocks>");
                    }
                    int blocks = Integer.parseInt(args[1]);
                    islandManager.expandIsland(playerId, blocks);
                    return CommandResult.success("&a[Island] Island expanded by &e" + blocks + " &ablocks!");
                }
                case "coop": {
                    if (args.length < 3) {
                        return CommandResult.usage("/is coop <add|remove> <player_uuid>");
                    }
                    String action = args[1].toLowerCase();
                    UUID target = UUID.fromString(args[2]);
                    if ("add".equals(action)) {
                        islandManager.addCoopMember(playerId, target);
                        return CommandResult.success("&a[Island] Added co-op member: &e" + target);
                    } else if ("remove".equals(action)) {
                        islandManager.removeMember(playerId, target);
                        return CommandResult.success("&a[Island] Removed member: &e" + target);
                    } else {
                        return CommandResult.usage("/is coop <add|remove> <player_uuid>");
                    }
                }
                case "info": {
                    Optional<IslandProfile> islandOpt = islandManager.getPlayerIsland(playerId);
                    if (!islandOpt.isPresent()) {
                        return CommandResult.error("You do not have an island yet. Use /is create to start!");
                    }
                    IslandProfile island = islandOpt.get();
                    sender.sendMessage("&6=== &eIsland Info &6===");
                    sender.sendMessage("&7Island ID: &f" + island.getIslandId());
                    sender.sendMessage("&7Owner ID: &f" + island.getOwnerId());
                    sender.sendMessage("&7Level: &a" + island.getLevel());
                    sender.sendMessage("&7Size: &a" + island.getSize() + "x" + island.getSize());
                    sender.sendMessage("&7Biome: &b" + island.getBiome());
                    sender.sendMessage("&7Home: &e" + island.getHomeWarp());
                    sender.sendMessage("&7Members: &f" + island.getMembers().size());
                    for (Map.Entry<UUID, IslandMemberRole> m : island.getMembers().entrySet()) {
                        sender.sendMessage("  &8- &7" + m.getKey() + ": &e" + m.getValue());
                    }
                    return CommandResult.success();
                }
                default:
                    return CommandResult.usage(getUsage());
            }
        } catch (IllegalArgumentException e) {
            return CommandResult.error(e.getMessage());
        }
    }
}
