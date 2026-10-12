package com.mcoin.skyblock.core.command;

import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.storage.StorageService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class MCoinAdminCommand implements MCoinCommand {
    private final EconomyService economyService;
    private final StorageService storageService;
    private final Runnable reloadAction;

    public MCoinAdminCommand(final EconomyService economyService,
                             final StorageService storageService,
                             final Runnable reloadAction) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
        this.storageService = Objects.requireNonNull(storageService, "storageService");
        this.reloadAction = reloadAction;
    }

    @Override
    public String getName() {
        return "mcoinadmin";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("mca", "madmin");
    }

    @Override
    public String getPermission() {
        return "mcoin.admin";
    }

    @Override
    public String getDescription() {
        return "Administrative control command for MCoin Skyblock package.";
    }

    @Override
    public String getUsage() {
        return "/mcoinadmin <reload|save|status|give|take>";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (args == null || args.length == 0) {
            sender.sendMessage("&6=== &eMCoin Admin Commands &6===");
            sender.sendMessage("&e/mcoinadmin reload &7- Reload configuration");
            sender.sendMessage("&e/mcoinadmin save &7- Flush and save all data to storage");
            sender.sendMessage("&e/mcoinadmin status &7- Show server & module status");
            sender.sendMessage("&e/mcoinadmin give <uuid> <mcoin|lidya> <amount> &7- Give currency");
            sender.sendMessage("&e/mcoinadmin take <uuid> <mcoin|lidya> <amount> &7- Take currency");
            return CommandResult.usage(getUsage());
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "save": {
                storageService.flush();
                return CommandResult.success("&a[MCoin] All player and island data successfully flushed to storage.");
            }
            case "reload": {
                if (reloadAction != null) {
                    reloadAction.run();
                }
                return CommandResult.success("&a[MCoin] Configuration reloaded successfully.");
            }
            case "status": {
                sender.sendMessage("&6=== &eMCoin Status &6===");
                sender.sendMessage("&7Total Cached Accounts: &a" + economyService.snapshot().size());
                return CommandResult.success();
            }
            case "give": {
                if (args.length < 4) {
                    return CommandResult.usage("/mcoinadmin give <uuid> <mcoin|lidya> <amount>");
                }
                try {
                    UUID target = UUID.fromString(args[1]);
                    CurrencyType type = CurrencyType.valueOf(args[2].toUpperCase());
                    BigDecimal amount = new BigDecimal(args[3]);
                    economyService.deposit(target, type, amount);
                    return CommandResult.success("&a[MCoin] Gave &e" + amount + " " + type + " &ato player &e" + target);
                } catch (IllegalArgumentException e) {
                    return CommandResult.error("Invalid arguments: " + e.getMessage());
                }
            }
            case "take": {
                if (args.length < 4) {
                    return CommandResult.usage("/mcoinadmin take <uuid> <mcoin|lidya> <amount>");
                }
                try {
                    UUID target = UUID.fromString(args[1]);
                    CurrencyType type = CurrencyType.valueOf(args[2].toUpperCase());
                    BigDecimal amount = new BigDecimal(args[3]);
                    economyService.withdraw(target, type, amount);
                    return CommandResult.success("&a[MCoin] Took &e" + amount + " " + type + " &afrom player &e" + target);
                } catch (IllegalArgumentException e) {
                    return CommandResult.error("Invalid arguments: " + e.getMessage());
                }
            }
            default:
                return CommandResult.usage(getUsage());
        }
    }
}
