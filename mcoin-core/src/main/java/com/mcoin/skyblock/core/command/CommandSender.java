package com.mcoin.skyblock.core.command;

import java.util.UUID;

public interface CommandSender {
    String getName();
    UUID getUniqueId();
    boolean hasPermission(String permission);
    void sendMessage(String message);
    boolean isPlayer();
    boolean isConsole();
}
