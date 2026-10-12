package com.mcoin.skyblock.core.command;

import java.util.Objects;

public final class CommandResult {
    public enum Status {
        SUCCESS,
        USAGE,
        NO_PERMISSION,
        NOT_A_PLAYER,
        NOT_FOUND,
        ERROR
    }

    private final Status status;
    private final String message;

    private CommandResult(final Status status, final String message) {
        this.status = Objects.requireNonNull(status, "status");
        this.message = message;
    }

    public static CommandResult success(final String message) {
        return new CommandResult(Status.SUCCESS, message);
    }

    public static CommandResult success() {
        return new CommandResult(Status.SUCCESS, null);
    }

    public static CommandResult usage(final String usageMessage) {
        return new CommandResult(Status.USAGE, usageMessage);
    }

    public static CommandResult noPermission(final String message) {
        return new CommandResult(Status.NO_PERMISSION, message != null ? message : "You do not have permission to use this command.");
    }

    public static CommandResult notAPlayer() {
        return new CommandResult(Status.NOT_A_PLAYER, "Only in-game players can execute this command.");
    }

    public static CommandResult notFound(final String command) {
        return new CommandResult(Status.NOT_FOUND, "Unknown command: " + command);
    }

    public static CommandResult error(final String errorMessage) {
        return new CommandResult(Status.ERROR, errorMessage);
    }

    public static CommandResult failure(final String errorMessage) {
        return error(errorMessage);
    }

    public Status getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public String getMessage() {
        return message;
    }
}
