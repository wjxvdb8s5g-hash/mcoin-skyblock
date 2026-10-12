package com.mcoin.skyblock.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class SimplePlayerCommandSender implements CommandSender {
    private final UUID uniqueId;
    private final String name;
    private final Set<String> permissions = new HashSet<String>();
    private final Consumer<String> messageConsumer;
    private final List<String> receivedMessages = new ArrayList<String>();

    public SimplePlayerCommandSender(final UUID uniqueId, final String name) {
        this(uniqueId, name, null);
    }

    public SimplePlayerCommandSender(final UUID uniqueId, final String name, final Consumer<String> messageConsumer) {
        this.uniqueId = Objects.requireNonNull(uniqueId, "uniqueId");
        this.name = Objects.requireNonNull(name, "name");
        this.messageConsumer = messageConsumer;
    }

    public void addPermission(final String permission) {
        permissions.add(Objects.requireNonNull(permission, "permission").toLowerCase());
    }

    public void removePermission(final String permission) {
        permissions.remove(Objects.requireNonNull(permission, "permission").toLowerCase());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean hasPermission(final String permission) {
        if (permission == null || permission.isEmpty()) return true;
        if (permissions.contains("*") || permissions.contains("mcoin.*")) return true;
        return permissions.contains(permission.toLowerCase());
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
        return true;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    public List<String> getReceivedMessages() {
        return Collections.unmodifiableList(new ArrayList<String>(receivedMessages));
    }

    public void clearMessages() {
        receivedMessages.clear();
    }
}
