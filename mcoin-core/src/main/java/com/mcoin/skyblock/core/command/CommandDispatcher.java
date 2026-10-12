package com.mcoin.skyblock.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CommandDispatcher {
    private final Map<String, MCoinCommand> commands = new LinkedHashMap<String, MCoinCommand>();

    public synchronized void register(final MCoinCommand command) {
        Objects.requireNonNull(command, "command");
        String name = command.getName().toLowerCase();
        commands.put(name, command);
        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                if (alias != null && !alias.trim().isEmpty()) {
                    commands.put(alias.toLowerCase().trim(), command);
                }
            }
        }
    }

    public synchronized Optional<MCoinCommand> findCommand(final String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(commands.get(name.toLowerCase().trim()));
    }

    public synchronized List<MCoinCommand> getCommands() {
        return Collections.unmodifiableList(new ArrayList<MCoinCommand>(commands.values()));
    }

    public CommandResult dispatch(final CommandSender sender, final String commandLine) {
        Objects.requireNonNull(sender, "sender");
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return CommandResult.usage("Please enter a command.");
        }
        String clean = commandLine.trim();
        if (clean.startsWith("/")) {
            clean = clean.substring(1);
        }
        String[] parts = clean.split("\\s+");
        String label = parts[0];
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        return dispatch(sender, label, args);
    }

    public CommandResult dispatch(final CommandSender sender, final String label, final String[] args) {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(label, "label");
        String[] safeArgs = args != null ? args : new String[0];

        MCoinCommand command;
        synchronized (this) {
            command = commands.get(label.toLowerCase().trim());
        }

        if (command == null) {
            CommandResult res = CommandResult.notFound(label);
            sender.sendMessage("&c" + res.getMessage());
            return res;
        }

        String permission = command.getPermission();
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            CommandResult res = CommandResult.noPermission(null);
            sender.sendMessage("&c" + res.getMessage());
            return res;
        }

        try {
            CommandResult result = command.execute(sender, safeArgs);
            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                sender.sendMessage(result.getMessage());
            }
            return result;
        } catch (Exception e) {
            String err = "An error occurred while executing command: " + e.getMessage();
            sender.sendMessage("&c" + err);
            return CommandResult.error(err);
        }
    }
}
