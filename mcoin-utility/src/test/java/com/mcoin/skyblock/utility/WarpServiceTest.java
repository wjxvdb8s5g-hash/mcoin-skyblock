package com.mcoin.skyblock.utility;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.SimplePlayerCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WarpServiceTest {

    private WarpService warpService;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        warpService = new WarpService();
        dispatcher = new CommandDispatcher();
        dispatcher.register(new WarpCommand(warpService));
    }

    @Test
    void testWarpManagement() {
        assertTrue(warpService.getWarp("spawn").isPresent());
        assertTrue(warpService.getWarp("shop").isPresent());

        warpService.setWarp(new WarpPoint("arena", "world", 50, 64, 50, "mcoin.warp.arena"));
        assertTrue(warpService.getWarp("arena").isPresent());

        assertTrue(warpService.deleteWarp("arena"));
        assertFalse(warpService.getWarp("arena").isPresent());
    }

    @Test
    void testWarpCommand() {
        UUID player = UUID.randomUUID();
        SimplePlayerCommandSender sender = new SimplePlayerCommandSender(player, "Teleporter");
        sender.addPermission("mcoin.warp.use");

        CommandResult listRes = dispatcher.dispatch(sender, "warp list");
        assertTrue(listRes.isSuccess());

        CommandResult tpRes = dispatcher.dispatch(sender, "warp spawn");
        assertTrue(tpRes.isSuccess());

        warpService.setWarp(new WarpPoint("vip_zone", "world", 0, 100, 0, "mcoin.warp.vip"));
        CommandResult failPerm = dispatcher.dispatch(sender, "warp vip_zone");
        assertFalse(failPerm.isSuccess());

        sender.addPermission("mcoin.warp.vip");
        CommandResult succPerm = dispatcher.dispatch(sender, "warp vip_zone");
        assertTrue(succPerm.isSuccess());
    }
}
