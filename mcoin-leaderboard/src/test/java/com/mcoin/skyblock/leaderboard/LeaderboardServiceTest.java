package com.mcoin.skyblock.leaderboard;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.ConsoleCommandSender;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardServiceTest {

    private IslandService islandService;
    private EconomyService economyService;
    private LeaderboardService leaderboardService;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        islandService = new IslandService();
        economyService = new EconomyService();
        leaderboardService = new LeaderboardService(islandService, economyService);
        dispatcher = new CommandDispatcher();
        dispatcher.register(new LeaderboardCommand(leaderboardService));
    }

    @Test
    void testTopIslandsAndCurrency() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        IslandProfile is1 = islandService.createIsland(p1, "LowIsland");
        is1.setLevel(5);

        IslandProfile is2 = islandService.createIsland(p2, "HighIsland");
        is2.setLevel(50);

        List<LeaderboardEntry> topIslands = leaderboardService.getTopIslands(10);
        assertEquals(2, topIslands.size());
        assertEquals(is2.getIslandId(), topIslands.get(0).getId());
        assertEquals(1, topIslands.get(0).getRank());
        assertEquals(BigDecimal.valueOf(50), topIslands.get(0).getScore());

        economyService.deposit(p1, CurrencyType.LIDYA, new BigDecimal("1000"));
        economyService.deposit(p2, CurrencyType.LIDYA, new BigDecimal("50000"));

        List<LeaderboardEntry> topLidya = leaderboardService.getTopCurrency(CurrencyType.LIDYA, 10);
        assertEquals(2, topLidya.size());
        assertEquals(p2, topLidya.get(0).getId());
    }

    @Test
    void testLeaderboardCommand() {
        ConsoleCommandSender console = new ConsoleCommandSender();
        CommandResult res = dispatcher.dispatch(console, "top islands");
        assertTrue(res.isSuccess());

        CommandResult lidyaRes = dispatcher.dispatch(console, "top lidya");
        assertTrue(lidyaRes.isSuccess());
    }
}
