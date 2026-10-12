package com.mcoin.skyblock.economy.command;

import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.CommandSender;
import com.mcoin.skyblock.core.command.MCoinCommand;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.economy.TradeService;
import com.mcoin.skyblock.economy.TradeSession;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TradeCommand implements MCoinCommand {
    private final TradeService tradeService;

    public TradeCommand(final TradeService tradeService) {
        this.tradeService = Objects.requireNonNull(tradeService, "tradeService");
    }

    @Override
    public String getName() {
        return "trade";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("takas");
    }

    @Override
    public String getPermission() {
        return "mcoin.economy.trade";
    }

    @Override
    public String getDescription() {
        return "Safe player-to-player trading system.";
    }

    @Override
    public String getUsage() {
        return "/trade <request <player_uuid>|offer <session_uuid> <mcoin|lidya> <amount>|confirm <session_uuid>|cancel <session_uuid>>";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        if (!sender.isPlayer()) {
            return CommandResult.notAPlayer();
        }
        UUID playerId = sender.getUniqueId();

        if (args == null || args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            sender.sendMessage("&6=== &ePlayer Trade Commands &6===");
            sender.sendMessage("&e/trade request <player_uuid> &7- Start trade with player");
            sender.sendMessage("&e/trade offer <session_id> <currency> <amount> &7- Offer currency");
            sender.sendMessage("&e/trade confirm <session_id> &7- Confirm trade");
            sender.sendMessage("&e/trade cancel <session_id> &7- Cancel trade");
            return CommandResult.success();
        }

        String sub = args[0].toLowerCase();
        try {
            if ("request".equals(sub)) {
                if (args.length < 2) {
                    return CommandResult.usage("/trade request <player_uuid>");
                }
                UUID target = UUID.fromString(args[1]);
                TradeSession session = tradeService.createSession(playerId, target);
                return CommandResult.success("&a[Trade] Created trade session: &e" + session.getSessionId());
            }

            if ("offer".equals(sub)) {
                if (args.length < 4) {
                    return CommandResult.usage("/trade offer <session_id> <mcoin|lidya> <amount>");
                }
                UUID sessionId = UUID.fromString(args[1]);
                CurrencyType currency = CurrencyType.valueOf(args[2].toUpperCase());
                BigDecimal amount = new BigDecimal(args[3]);
                tradeService.offer(sessionId, playerId, currency, amount);
                return CommandResult.success("&a[Trade] Offered &e" + amount + " " + currency.name() + " &ain session &e" + sessionId);
            }

            if ("confirm".equals(sub)) {
                if (args.length < 2) {
                    return CommandResult.usage("/trade confirm <session_id>");
                }
                UUID sessionId = UUID.fromString(args[1]);
                tradeService.confirm(sessionId, playerId);
                TradeSession updated = tradeService.findSession(sessionId).orElse(null);
                if (updated != null && updated.isReady()) {
                    tradeService.execute(sessionId);
                    return CommandResult.success("&a[Trade] Both players confirmed! Trade session &e" + sessionId + " &aexecuted successfully.");
                }
                return CommandResult.success("&a[Trade] You confirmed trade session &e" + sessionId + ". Waiting for other player...");
            }

            if ("cancel".equals(sub)) {
                if (args.length < 2) {
                    return CommandResult.usage("/trade cancel <session_id>");
                }
                UUID sessionId = UUID.fromString(args[1]);
                tradeService.cancel(sessionId);
                return CommandResult.success("&c[Trade] Trade session &e" + sessionId + " &ccancelled.");
            }
        } catch (IllegalArgumentException e) {
            return CommandResult.error(e.getMessage());
        }

        return CommandResult.usage(getUsage());
    }
}
