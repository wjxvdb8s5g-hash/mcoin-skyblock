package com.mcoin.skyblock.island;

import com.mcoin.skyblock.core.config.IslandConfig;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;
import com.mcoin.skyblock.core.island.IslandMemberRole;
import com.mcoin.skyblock.core.island.IslandProfile;
import com.mcoin.skyblock.core.island.IslandService;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class IslandManager {
    private final IslandService islandService;
    private final EconomyService economyService;
    private final IslandConfig config;

    public IslandManager(final IslandService islandService,
                         final EconomyService economyService,
                         final IslandConfig config) {
        this.islandService = Objects.requireNonNull(islandService, "islandService");
        this.economyService = Objects.requireNonNull(economyService, "economyService");
        this.config = Objects.requireNonNull(config, "config");
    }

    public synchronized IslandProfile createIsland(final UUID ownerId, final String biome) {
        String safeBiome = (biome == null || biome.trim().isEmpty()) ? config.getDefaultBiome() : biome.trim().toUpperCase();
        return islandService.createIsland(ownerId, safeBiome);
    }

    public synchronized Optional<IslandProfile> getPlayerIsland(final UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Optional<IslandProfile> owned = islandService.findByOwner(playerId);
        if (owned.isPresent()) {
            return owned;
        }
        for (IslandProfile island : islandService.snapshot().values()) {
            if (island.getMembers().containsKey(playerId)) {
                return Optional.of(island);
            }
        }
        return Optional.empty();
    }

    public synchronized void upgradeLevel(final UUID playerId, final int levels) {
        if (levels <= 0) {
            throw new IllegalArgumentException("levels must be positive");
        }
        IslandProfile island = requireIsland(playerId);
        BigDecimal cost = config.getUpgradeCostPerLevel().multiply(BigDecimal.valueOf(levels));
        economyService.withdraw(playerId, CurrencyType.LIDYA, cost);
        islandService.upgradeIslandLevel(island.getIslandId(), levels);
    }

    public synchronized void expandIsland(final UUID playerId, final int blocks) {
        if (blocks <= 0) {
            throw new IllegalArgumentException("blocks must be positive");
        }
        IslandProfile island = requireIsland(playerId);
        if (island.getSize() + blocks > config.getMaxIslandSize()) {
            throw new IllegalArgumentException("expansion exceeds maximum allowed island size (" + config.getMaxIslandSize() + ")");
        }
        BigDecimal cost = config.getExpansionCostPerBlock().multiply(BigDecimal.valueOf(blocks));
        economyService.withdraw(playerId, CurrencyType.LIDYA, cost);
        islandService.expandIsland(island.getIslandId(), blocks);
    }

    public synchronized void changeBiome(final UUID playerId, final String newBiome) {
        Objects.requireNonNull(newBiome, "newBiome");
        IslandProfile island = requireIsland(playerId);
        BigDecimal cost = config.getBiomeChangeCost();
        economyService.withdraw(playerId, CurrencyType.LIDYA, cost);
        islandService.setBiome(island.getIslandId(), newBiome.trim().toUpperCase());
    }

    public synchronized void setHomeWarp(final UUID playerId, final String warpName) {
        IslandProfile island = requireIsland(playerId);
        islandService.setHomeWarp(island.getIslandId(), warpName);
    }

    public synchronized String getHomeWarp(final UUID playerId) {
        return requireIsland(playerId).getHomeWarp();
    }

    public synchronized void addCoopMember(final UUID ownerId, final UUID targetPlayerId) {
        IslandProfile island = requireOwnedIsland(ownerId);
        if (island.getMembers().size() >= config.getMaxCoopMembers()) {
            throw new IllegalArgumentException("Maximum co-op members reached (" + config.getMaxCoopMembers() + ")");
        }
        islandService.addCoopMember(island.getIslandId(), targetPlayerId);
    }

    public synchronized void removeMember(final UUID ownerId, final UUID targetPlayerId) {
        IslandProfile island = requireOwnedIsland(ownerId);
        islandService.removeMember(island.getIslandId(), targetPlayerId);
    }

    private IslandProfile requireIsland(final UUID playerId) {
        return getPlayerIsland(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player does not have or belong to an island: " + playerId));
    }

    private IslandProfile requireOwnedIsland(final UUID ownerId) {
        return islandService.findByOwner(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("Player does not own an island: " + ownerId));
    }

    public IslandService getIslandService() {
        return islandService;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

    public IslandConfig getConfig() {
        return config;
    }
}
