package com.mcoin.skyblock.economy.command;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.economy.BankService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class BankCommand implements MCoinCommand {
    private final BankService bankService;

    public BankCommand(final BankService bankService) {
        this.bankService = Objects.requireNonNull(bankService, "bankService");
    }

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("banka", "vault");
    }

    @Override
    public String getPermission() {
        return "mcoin.economy.bank";
    }

    @Override
    public String getDescription() {
        return "Bank vault operations: deposit, withdraw, and check bank balance.";
    }

    @Override
    public String getUsage() {
        return "/bank <balance|deposit <mcoin|lidya> <amount>|withdraw <mcoin|lidya> <amount>>";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.notAPlayer();
        }
        UUID playerId = sender.getUniqueId();

        if (args == null || args.length == 0 || "balance".equalsIgnoreCase(args[0]) || "bal".equalsIgnoreCase(args[0]) || "bakiye".equalsIgnoreCase(args[0])) {
            BigDecimal mcoin = bankService.balance(playerId, CurrencyType.MCOIN);
            BigDecimal lidya = bankService.balance(playerId, CurrencyType.LIDYA);
            sender.sendMessage("&6=== &eBank Vault &6===");
            sender.sendMessage("&7MCOIN in Bank: &e" + mcoin.toPlainString());
            sender.sendMessage("&7Lidya in Bank: &e" + lidya.toPlainString());
            return CommandResult.success();
        }

        String sub = args[0].toLowerCase();
        if ("deposit".equals(sub) || "yatir".equals(sub)) {
            if (args.length < 3) {
                return CommandResult.usage("/bank deposit <mcoin|lidya> <amount>");
            }
            try {
                CurrencyType type = CurrencyType.valueOf(args[1].toUpperCase());
                BigDecimal amount = new BigDecimal(args[2]);
                bankService.deposit(playerId, type, amount);
                return CommandResult.success("&a[Bank] Deposited &e" + amount + " " + type.name() + " &ainto your bank vault.");
            } catch (IllegalArgumentException e) {
                return CommandResult.error(e.getMessage());
            }
        }

        if ("withdraw".equals(sub) || "cek".equals(sub)) {
            if (args.length < 3) {
                return CommandResult.usage("/bank withdraw <mcoin|lidya> <amount>");
            }
            try {
                CurrencyType type = CurrencyType.valueOf(args[1].toUpperCase());
                BigDecimal amount = new BigDecimal(args[2]);
                bankService.withdraw(playerId, type, amount);
                return CommandResult.success("&a[Bank] Withdrew &e" + amount + " " + type.name() + " &afrom your bank vault.");
            } catch (IllegalArgumentException e) {
                return CommandResult.error(e.getMessage());
            }
        }

        return CommandResult.usage(getUsage());
    }
}
