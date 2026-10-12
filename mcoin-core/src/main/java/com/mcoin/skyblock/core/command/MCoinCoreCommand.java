package com.mcoin.skyblock.core.command;

import com.mcoin.skyblock.core.plugin.PluginDescriptor;
import com.mcoin.skyblock.core.plugin.PluginRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class MCoinCoreCommand implements MCoinCommand {
    private final PluginRegistry registry;

    public MCoinCoreCommand(final PluginRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public String getName() {
        return "mcoincore";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("mcoin", "mcc", "mcore");
    }

    @Override
    public String getPermission() {
        return "mcoin.core.info";
    }

    @Override
    public String getDescription() {
        return "Displays MCoin Skyblock package information and active modules.";
    }

    @Override
    public String getUsage() {
        return "/mcoincore";
    }

    @Override
    public CommandResult execute(final CommandSender sender, final String[] args) {
        sender.sendMessage("&6=== &eMCoin Skyblock Package &6===");
        sender.sendMessage("&7Version: &f1.0.0-SNAPSHOT (Java 8, 1.8.x - 1.21.x)");
        sender.sendMessage("&7Registered Modules: &a" + registry.list().size());
        for (PluginDescriptor desc : registry.list()) {
            sender.sendMessage("&8- &e" + desc.getName() + " &7(" + desc.getDisplayName() + "): &f" + desc.getDescription());
        }
        return CommandResult.success();
    }
}
