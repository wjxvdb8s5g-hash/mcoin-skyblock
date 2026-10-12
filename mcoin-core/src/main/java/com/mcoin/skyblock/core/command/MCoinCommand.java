package com.mcoin.skyblock.core.command;

import java.util.Collections;
import java.util.List;

public interface MCoinCommand {
    String getName();
    List<String> getAliases();
    String getPermission();
    String getDescription();
    String getUsage();
    CommandResult execute(CommandSender sender, String[] args);
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
