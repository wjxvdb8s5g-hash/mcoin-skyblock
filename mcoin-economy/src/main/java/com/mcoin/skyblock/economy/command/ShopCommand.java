package com.mcoin.skyblock.economy.command;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.economy.ServerShopService;
import com.mcoin.skyblock.economy.ShopOffer;
import com.mcoin.skyblock.economy.ShopTransactionReceipt;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ShopCommand implements MCoinCommand {
    private final ServerShopService shopService;

    public ShopCommand(final ServerShopService shopService) {
        this.shopService = Objects.requireNonNull(shopService, "shopService");
    }

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("market", "magaza");
    }

    @Override
    public String getPermission() {
        return "mcoin.economy.shop";
    }

    @Override
    public String getDescription() {
        return "Browse and trade with the server economy shop.";
    }

    @Override
    public String getUsage() {
        return "/shop <list|buy <item> [quantity]|sell <item> [quantity]>";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.notAPlayer();
        }
        UUID playerId = sender.getUniqueId();

        if (args == null || args.length == 0 || "list".equalsIgnoreCase(args[0])) {
            Map<String, ShopOffer> offers = shopService.snapshot();
            sender.sendMessage("&6=== &eServer Shop Offers &6===");
            if (offers.isEmpty()) {
                sender.sendMessage("&7No offers available at the moment.");
            } else {
                for (ShopOffer offer : offers.values()) {
                    sender.sendMessage("&e" + offer.getItemKey() + " &7- Buy: &a" +
                        offer.getBuyPrice() + " " + offer.getCurrencyType().name() +
                        " &7| Sell: &c" + offer.getSellPrice() + " " + offer.getCurrencyType().name());
                }
            }
            return CommandResult.success();
        }

        String sub = args[0].toLowerCase();
        try {
            if ("buy".equals(sub) || "al".equals(sub)) {
                if (args.length < 2) {
                    return CommandResult.usage("/shop buy <item> [quantity]");
                }
                String itemKey = args[1];
                int qty = args.length > 2 ? Integer.parseInt(args[2]) : 1;
                ShopTransactionReceipt receipt = shopService.buy(playerId, itemKey, qty);
                return CommandResult.success("&a[Shop] Purchased &e" + receipt.getQuantity() + "x " +
                    receipt.getItemKey() + " &afor &e" + receipt.getTotalPrice() + " " +
                    receipt.getCurrencyType().name());
            }

            if ("sell".equals(sub) || "sat".equals(sub)) {
                if (args.length < 2) {
                    return CommandResult.usage("/shop sell <item> [quantity]");
                }
                String itemKey = args[1];
                int qty = args.length > 2 ? Integer.parseInt(args[2]) : 1;
                ShopTransactionReceipt receipt = shopService.sell(playerId, itemKey, qty);
                return CommandResult.success("&a[Shop] Sold &e" + receipt.getQuantity() + "x " +
                    receipt.getItemKey() + " &afor &e" + receipt.getTotalPrice() + " " +
                    receipt.getCurrencyType().name());
            }
        } catch (IllegalArgumentException e) {
            return CommandResult.error(e.getMessage());
        }

        return CommandResult.usage(getUsage());
    }
}
