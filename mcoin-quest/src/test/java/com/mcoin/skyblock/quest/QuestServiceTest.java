package com.mcoin.skyblock.quest;

import com.mcoin.skyblock.core.command.CommandDispatcher;
import com.mcoin.skyblock.core.command.CommandResult;
import com.mcoin.skyblock.core.command.SimplePlayerCommandSender;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestServiceTest {

    private EconomyService economyService;
    private QuestService questService;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        economyService = new EconomyService();
        questService = new QuestService(economyService);
        dispatcher = new CommandDispatcher();
        dispatcher.register(new QuestCommand(questService));
    }

    @Test
    void testQuestProgressAndClaim() {
        UUID player = UUID.randomUUID();
        QuestProgress progress = questService.getProgress(player, "daily_slayer");
        assertFalse(progress.isCompleted());
        assertFalse(progress.isClaimed());

        boolean completed = questService.recordProgress(player, "daily_slayer", 50);
        assertTrue(completed);
        assertTrue(progress.isCompleted());

        questService.claimReward(player, "daily_slayer");
        assertTrue(progress.isClaimed());
        assertEquals(new BigDecimal("500.00"), economyService.account(player).getBalance(CurrencyType.LIDYA));
        assertEquals(new BigDecimal("10.00"), economyService.account(player).getBalance(CurrencyType.MCOIN));
    }

    @Test
    void testQuestCommand() {
        UUID player = UUID.randomUUID();
        SimplePlayerCommandSender sender = new SimplePlayerCommandSender(player, "Quester");
        sender.addPermission("mcoin.quest.use");

        CommandResult listRes = dispatcher.dispatch(sender, "quest list");
        assertTrue(listRes.isSuccess());

        questService.recordProgress(player, "daily_mining", 128);

        CommandResult claimRes = dispatcher.dispatch(sender, "quest claim daily_mining");
        assertTrue(claimRes.isSuccess());
        assertTrue(questService.getProgress(player, "daily_mining").isClaimed());
    }
}
