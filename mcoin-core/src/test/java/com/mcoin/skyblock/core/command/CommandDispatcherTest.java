package com.mcoin.skyblock.core.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommandDispatcherTest {

    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new CommandDispatcher();
    }

    private static class DummyCommand implements MCoinCommand {
        private final String name;
        private final String permission;
        private final boolean playerOnly;

        DummyCommand(String name, String permission, boolean playerOnly) {
            this.name = name;
            this.permission = permission;
            this.playerOnly = playerOnly;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getAliases() {
            return Collections.singletonList(name + "alias");
        }

        @Override
        public String getPermission() {
            return permission;
        }

        @Override
        public String getDescription() {
            return "Test dummy command";
        }

        @Override
        public String getUsage() {
            return "/" + name;
        }

        @Override
        public CommandResult execute(CommandSender sender, String[] args) {
            if (playerOnly && !sender.isPlayer()) {
                return CommandResult.notAPlayer();
            }
            return CommandResult.success("Executed: " + String.join(" ", args));
        }
    }

    @Test
    void testCommandDispatchAndAlias() {
        DummyCommand cmd = new DummyCommand("test", "mcoin.test", false);
        dispatcher.register(cmd);

        assertTrue(dispatcher.findCommand("test").isPresent());
        assertTrue(dispatcher.findCommand("testalias").isPresent());

        ConsoleCommandSender console = new ConsoleCommandSender();
        CommandResult r1 = dispatcher.dispatch(console, "test", new String[]{"arg1", "arg2"});
        assertTrue(r1.isSuccess());
        assertEquals("Executed: arg1 arg2", r1.getMessage());

        CommandResult r2 = dispatcher.dispatch(console, "testalias", new String[]{"hello"});
        assertTrue(r2.isSuccess());
        assertEquals("Executed: hello", r2.getMessage());
    }

    @Test
    void testPermissionCheck() {
        DummyCommand cmd = new DummyCommand("vip", "mcoin.vip", false);
        dispatcher.register(cmd);

        SimplePlayerCommandSender playerWithoutPerm = new SimplePlayerCommandSender(
            UUID.randomUUID(), "Noob"
        );
        CommandResult r = dispatcher.dispatch(playerWithoutPerm, "vip", new String[0]);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("permission"));

        SimplePlayerCommandSender playerWithPerm = new SimplePlayerCommandSender(
            UUID.randomUUID(), "Pro"
        );
        playerWithPerm.addPermission("mcoin.vip");
        CommandResult rSuccess = dispatcher.dispatch(playerWithPerm, "vip", new String[0]);
        assertTrue(rSuccess.isSuccess());
    }

    @Test
    void testPlayerOnlyCheck() {
        DummyCommand cmd = new DummyCommand("fly", "mcoin.fly", true);
        dispatcher.register(cmd);

        ConsoleCommandSender console = new ConsoleCommandSender();
        CommandResult r = dispatcher.dispatch(console, "fly", new String[0]);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("Only in-game players"));

        SimplePlayerCommandSender player = new SimplePlayerCommandSender(
            UUID.randomUUID(), "Player1"
        );
        player.addPermission("mcoin.fly");
        CommandResult rPlayer = dispatcher.dispatch(player, "fly", new String[0]);
        assertTrue(rPlayer.isSuccess());
    }

    @Test
    void testUnknownCommand() {
        ConsoleCommandSender console = new ConsoleCommandSender();
        CommandResult r = dispatcher.dispatch(console, "unknown", new String[0]);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("Unknown command"));
    }
}
