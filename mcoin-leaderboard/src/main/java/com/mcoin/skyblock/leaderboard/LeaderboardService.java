package com.mcoin.skyblock.leaderboard;

import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class LeaderboardService {
    private final IslandService islandService;
    private final EconomyService economyService;

    public LeaderboardService(final IslandService islandService, final EconomyService economyService) {
        this.islandService = Objects.requireNonNull(islandService, "islandService");
        this.economyService = Objects.requireNonNull(economyService, "economyService");
    }

    public synchronized List<LeaderboardEntry> getTopIslands(final int limit) {
        int max = Math.max(1, limit);
        Map<UUID, IslandProfile> islands = islandService.snapshot();
        List<IslandProfile> sorted = new ArrayList<IslandProfile>(islands.values());
        Collections.sort(sorted, new Comparator<IslandProfile>() {
            @Override
            public int compare(final IslandProfile o1, final IslandProfile o2) {
                return Integer.compare(o2.getLevel(), o1.getLevel());
            }
        });

        List<LeaderboardEntry> entries = new ArrayList<LeaderboardEntry>();
        int rank = 1;
        for (IslandProfile island : sorted) {
            if (rank > max) break;
            entries.add(new LeaderboardEntry(
                rank,
                island.getIslandId(),
                "Island #" + island.getIslandId().toString().substring(0, 8),
                BigDecimal.valueOf(island.getLevel()),
                "Level " + island.getLevel() + " (Size: " + island.getSize() + ")"
            ));
            rank++;
        }
        return entries;
    }

    public synchronized List<LeaderboardEntry> getTopCurrency(final CurrencyType currencyType, final int limit) {
        Objects.requireNonNull(currencyType, "currencyType");
        int max = Math.max(1, limit);
        Map<UUID, CurrencyAccount> accounts = economyService.snapshot();
        List<CurrencyAccount> sorted = new ArrayList<CurrencyAccount>(accounts.values());
        Collections.sort(sorted, new Comparator<CurrencyAccount>() {
            @Override
            public int compare(final CurrencyAccount o1, final CurrencyAccount o2) {
                return o2.getBalance(currencyType).compareTo(o1.getBalance(currencyType));
            }
        });

        List<LeaderboardEntry> entries = new ArrayList<LeaderboardEntry>();
        int rank = 1;
        for (CurrencyAccount acc : sorted) {
            if (rank > max) break;
            BigDecimal bal = acc.getBalance(currencyType);
            entries.add(new LeaderboardEntry(
                rank,
                acc.getPlayerId(),
                "Player " + acc.getPlayerId().toString().substring(0, 8),
                bal,
                bal.toPlainString() + " " + currencyType.name()
            ));
            rank++;
        }
        return entries;
    }
}
