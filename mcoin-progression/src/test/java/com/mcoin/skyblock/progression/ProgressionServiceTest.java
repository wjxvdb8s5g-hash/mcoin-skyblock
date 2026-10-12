package com.mcoin.skyblock.progression;

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

class ProgressionServiceTest {

    private EconomyService economyService;
    private ProgressionService progressionService;
    private CommandDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        economyService = new EconomyService();
        progressionService = new ProgressionService(economyService);
        dispatcher = new CommandDispatcher();
        dispatcher.register(new SkillsCommand(progressionService));
        dispatcher.register(new RankCommand(progressionService));
    }

    @Test
    void testProgressionXpAndRanks() {
        UUID player = UUID.randomUUID();
        PlayerProgression profile = progressionService.getProgression(player);
        assertEquals(RankType.MEMBER, profile.getRank());
        assertEquals(1, profile.getLevel(SkillType.MINING));

        boolean leveledUp = progressionService.awardSkillXp(player, SkillType.MINING, 250.0);
        assertTrue(leveledUp);
        assertEquals(2, profile.getLevel(SkillType.MINING));

        economyService.deposit(player, CurrencyType.MCOIN, new BigDecimal("1000"));
        progressionService.purchaseRank(player, RankType.VIP);
        assertEquals(RankType.VIP, profile.getRank());
    }

    @Test
    void testProgressionCommands() {
        UUID player = UUID.randomUUID();
        economyService.deposit(player, CurrencyType.MCOIN, new BigDecimal("5000"));

        SimplePlayerCommandSender sender = new SimplePlayerCommandSender(player, "Grinder");
        sender.addPermission("mcoin.skills.use");
        sender.addPermission("mcoin.rank.use");

        CommandResult skillsRes = dispatcher.dispatch(sender, "skills");
        assertTrue(skillsRes.isSuccess());

        CommandResult ranksRes = dispatcher.dispatch(sender, "rank list");
        assertTrue(ranksRes.isSuccess());

        CommandResult buyRes = dispatcher.dispatch(sender, "rank buy VIP");
        assertTrue(buyRes.isSuccess());
        assertEquals(RankType.VIP, progressionService.getProgression(player).getRank());
    }
}
