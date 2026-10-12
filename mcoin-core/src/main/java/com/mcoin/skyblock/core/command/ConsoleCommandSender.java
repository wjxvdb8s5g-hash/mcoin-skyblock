package com.mcoin.skyblock.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class ConsoleCommandSender implements CommandSender {
    private final Consumer<String> messageConsumer;
    private final List<String> receivedMessages = new ArrayList<String>();

    public ConsoleCommandSender() {
        this(null);
    }

    public ConsoleCommandSender(final Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public boolean hasPermission(final String permission) {
        return true;
    }

    @Override
    public void sendMessage(final String message) {
        String safeMessage = Objects.requireNonNull(message, "message");
        receivedMessages.add(safeMessage);
        if (messageConsumer != null) {
            messageConsumer.accept(safeMessage);
        }
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    public List<String> getReceivedMessages() {
        return Collections.unmodifiableList(new ArrayList<String>(receivedMessages));
    }

    public void clearMessages() {
        receivedMessages.clear();
    }
}
