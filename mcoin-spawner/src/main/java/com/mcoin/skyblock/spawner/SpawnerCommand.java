package com.mcoin.skyblock.spawner;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class SpawnerCommand implements MCoinCommand {
    private final SpawnerService spawnerService;
    private final IslandService islandService;

    public SpawnerCommand(final SpawnerService spawnerService, final IslandService islandService) {
        this.spawnerService = Objects.requireNonNull(spawnerService, "spawnerService");
        this.islandService = Objects.requireNonNull(islandService, "islandService");
    }

    @Override
    public String getName() {
        return "spawner";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("spawners");
    }

    @Override
    public String getPermission() {
        return "mcoin.spawner.use";
    }

    @Override
    public String getDescription() {
        return "Manage island mob spawners, upgrades, and stacking.";
    }

    @Override
    public String getUsage() {
        return "/spawner <list|buy|upgrade|stack|info>";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.failure("Only players can use /spawner commands.");
        }
        final UUID playerId = sender.getUniqueId();

        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            return CommandResult.success(
                "&b=== &6MCoin Spawners &b===\n" +
                "&e/spawner list &7- View available spawner types\n" +
                "&e/spawner buy <type> &7- Purchase and place spawner on your island\n" +
                "&e/spawner upgrade <spawnerId> &7- Upgrade spawner tier (max 5)\n" +
                "&e/spawner stack <spawnerId> <amount> &7- Stack additional spawners (max 64)\n" +
                "&e/spawner info <spawnerId> &7- View details about a spawner"
            );
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list": {
                StringBuilder sb = new StringBuilder("&b=== &6Available Spawner Types &b===\n");
                for (SpawnerType st : SpawnerType.values()) {
                    sb.append("&e").append(st.name())
                      .append(" &7(").append(st.getDisplayName()).append(") - ")
                      .append("&a").append(st.getBaseCost().toPlainString()).append(" Lidya")
                      .append(" &7[Req Island Lvl: &b").append(st.getMinIslandLevel()).append("&7]\n");
                }
                return CommandResult.success(sb.toString().trim());
            }
            case "buy": {
                if (args.length < 2) {
                    return CommandResult.failure("Usage: /spawner buy <type>");
                }
                SpawnerType type;
                try {
                    type = SpawnerType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    return CommandResult.failure("Invalid spawner type: " + args[1] + ". Use /spawner list.");
                }

                IslandProfile island = islandService.findByOwner(playerId).orElse(null);
                if (island == null) {
                    return CommandResult.failure("You must have an island to purchase spawners. Use /island create.");
                }

                try {
                    SpawnerInstance instance = spawnerService.purchaseSpawner(playerId, island.getIslandId(), type, island.getLevel());
                    return CommandResult.success("&aPurchased " + type.getDisplayName() + " spawner for &e" +
                        type.getBaseCost() + " Lidya&a! ID: &e" + instance.getSpawnerId());
                } catch (Exception ex) {
                    return CommandResult.failure(ex.getMessage());
                }
            }
            case "upgrade": {
                if (args.length < 2) {
                    return CommandResult.failure("Usage: /spawner upgrade <spawnerId>");
                }
                UUID spawnerId;
                try {
                    spawnerId = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    return CommandResult.failure("Invalid spawner UUID.");
                }
                try {
                    spawnerService.upgradeTier(playerId, spawnerId);
                    SpawnerInstance spawner = spawnerService.findSpawner(spawnerId).orElse(null);
                    return CommandResult.success("&aSpawner upgraded to Tier &e" + (spawner != null ? spawner.getTier() : "?") + "&a!");
                } catch (Exception ex) {
                    return CommandResult.failure(ex.getMessage());
                }
            }
            case "stack": {
                if (args.length < 3) {
                    return CommandResult.failure("Usage: /spawner stack <spawnerId> <amount>");
                }
                UUID spawnerId;
                int amount;
                try {
                    spawnerId = UUID.fromString(args[1]);
                    amount = Integer.parseInt(args[2]);
                } catch (IllegalArgumentException e) {
                    return CommandResult.failure("Invalid spawner UUID or amount.");
                }
                try {
                    spawnerService.stackSpawner(playerId, spawnerId, amount);
                    SpawnerInstance spawner = spawnerService.findSpawner(spawnerId).orElse(null);
                    return CommandResult.success("&aStacked &e" + amount + " &aspawners. Total stack: &e" + (spawner != null ? spawner.getStackCount() : "?"));
                } catch (Exception ex) {
                    return CommandResult.failure(ex.getMessage());
                }
            }
            case "info": {
                if (args.length < 2) {
                    return CommandResult.failure("Usage: /spawner info <spawnerId>");
                }
                UUID spawnerId;
                try {
                    spawnerId = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    return CommandResult.failure("Invalid spawner UUID.");
                }
                SpawnerInstance spawner = spawnerService.findSpawner(spawnerId).orElse(null);
                if (spawner == null) {
                    return CommandResult.failure("Spawner not found.");
                }
                return CommandResult.success(
                    "&b=== &6Spawner Details &b===\n" +
                    "&7ID: &e" + spawner.getSpawnerId() + "\n" +
                    "&7Type: &e" + spawner.getType().getDisplayName() + "\n" +
                    "&7Tier: &e" + spawner.getTier() + "/5 (Spawn speed x" + spawner.getSpawnRateMultiplier() + ", Drops x" + spawner.getDropMultiplier() + ")\n" +
                    "&7Stack Count: &e" + spawner.getStackCount() + "/64\n" +
                    "&7Island: &e" + spawner.getIslandId()
                );
            }
            default:
                return CommandResult.failure("Unknown spawner action: " + sub + ". Use /spawner help.");
        }
    }
}
