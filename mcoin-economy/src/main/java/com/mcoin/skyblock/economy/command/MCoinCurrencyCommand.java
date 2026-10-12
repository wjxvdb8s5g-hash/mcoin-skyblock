package com.mcoin.skyblock.economy.command;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class MCoinCurrencyCommand implements MCoinCommand {
    private final EconomyService economyService;
    private final CurrencyType currencyType;
    private final String commandName;
    private final List<String> aliases;

    public MCoinCurrencyCommand(final EconomyService economyService, final CurrencyType currencyType) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
        this.currencyType = Objects.requireNonNull(currencyType, "currencyType");
        this.commandName = currencyType.name().toLowerCase();
        this.aliases = currencyType == CurrencyType.MCOIN
            ? Arrays.asList("mc", "mcoins")
            : Arrays.asList("ld", "money", "bakiye");
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getPermission() {
        return "mcoin.economy." + commandName;
    }

    @Override
    public String getDescription() {
        return "View and transfer " + currencyType.name() + " balances.";
    }

    @Override
    public String getUsage() {
        return "/" + commandName + " [balance|pay <player_uuid> <amount>]";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.notAPlayer();
        }
        UUID playerId = sender.getUniqueId();

        if (args == null || args.length == 0 || "balance".equalsIgnoreCase(args[0]) || "bal".equalsIgnoreCase(args[0]) || "bakiye".equalsIgnoreCase(args[0])) {
            BigDecimal balance = economyService.account(playerId).getBalance(currencyType);
            sender.sendMessage("&a[Economy] Your " + currencyType.name() + " balance: &e" + balance.toPlainString());
            return CommandResult.success();
        }

        String sub = args[0].toLowerCase();
        if ("pay".equals(sub) || "gonder".equals(sub)) {
            if (args.length < 3) {
                return CommandResult.usage("/" + commandName + " pay <player_uuid> <amount>");
            }
            try {
                UUID target = UUID.fromString(args[1]);
                BigDecimal amount = new BigDecimal(args[2]);
                economyService.transfer(playerId, target, currencyType, amount);
                return CommandResult.success("&a[Economy] Successfully sent &e" + amount + " " + currencyType.name() + " &ato &e" + target);
            } catch (IllegalArgumentException e) {
                return CommandResult.error(e.getMessage());
            }
        }

        return CommandResult.usage(getUsage());
    }
}
