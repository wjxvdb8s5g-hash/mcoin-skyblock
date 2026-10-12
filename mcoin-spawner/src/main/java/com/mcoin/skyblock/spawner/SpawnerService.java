package com.mcoin.skyblock.spawner;

import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.currency.EconomyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class SpawnerService {
    private final EconomyService economyService;
    private final Map<UUID, SpawnerInstance> spawners = new LinkedHashMap<UUID, SpawnerInstance>();

    public SpawnerService(final EconomyService economyService) {
        this.economyService = Objects.requireNonNull(economyService, "economyService");
    }

    public synchronized SpawnerInstance purchaseSpawner(final UUID playerId,
                                                        final UUID islandId,
                                                        final SpawnerType type,
                                                        final int currentIslandLevel) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(islandId, "islandId");
        Objects.requireNonNull(type, "type");

        if (currentIslandLevel < type.getMinIslandLevel()) {
            throw new IllegalStateException("Island level " + currentIslandLevel + " is too low. Required: " + type.getMinIslandLevel());
        }

        BigDecimal cost = type.getBaseCost();
        economyService.withdraw(playerId, CurrencyType.LIDYA, cost);

        SpawnerInstance instance = new SpawnerInstance(UUID.randomUUID(), playerId, islandId, type, 1, 1);
        spawners.put(instance.getSpawnerId(), instance);
        return instance;
    }

    public synchronized void upgradeTier(final UUID playerId, final UUID spawnerId) {
        SpawnerInstance spawner = requireSpawner(spawnerId);
        if (!spawner.getOwnerId().equals(playerId)) {
            throw new IllegalArgumentException("You are not the owner of this spawner");
        }
        if (spawner.getTier() >= 5) {
            throw new IllegalStateException("Spawner is already at maximum tier 5");
        }

        BigDecimal upgradeCost = spawner.getType().getBaseCost().multiply(BigDecimal.valueOf(spawner.getTier()));
        economyService.withdraw(playerId, CurrencyType.LIDYA, upgradeCost);
        spawner.setTier(spawner.getTier() + 1);
    }

    public synchronized void stackSpawner(final UUID playerId, final UUID spawnerId, final int additionalCount) {
        if (additionalCount <= 0) {
            throw new IllegalArgumentException("Amount to stack must be positive");
        }
        SpawnerInstance spawner = requireSpawner(spawnerId);
        if (!spawner.getOwnerId().equals(playerId)) {
            throw new IllegalArgumentException("You are not the owner of this spawner");
        }
        if (spawner.getStackCount() + additionalCount > 64) {
            throw new IllegalStateException("Stack limit of 64 reached. Current: " + spawner.getStackCount());
        }

        BigDecimal stackCost = spawner.getType().getBaseCost().multiply(BigDecimal.valueOf(additionalCount));
        economyService.withdraw(playerId, CurrencyType.LIDYA, stackCost);
        spawner.setStackCount(spawner.getStackCount() + additionalCount);
    }

    public synchronized Optional<SpawnerInstance> findSpawner(final UUID spawnerId) {
        return Optional.ofNullable(spawners.get(spawnerId));
    }

    public synchronized List<SpawnerInstance> getSpawnersForIsland(final UUID islandId) {
        List<SpawnerInstance> result = new ArrayList<SpawnerInstance>();
        for (SpawnerInstance spawner : spawners.values()) {
            if (spawner.getIslandId().equals(islandId)) {
                result.add(spawner);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Map<UUID, SpawnerInstance> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<UUID, SpawnerInstance>(spawners));
    }

    private SpawnerInstance requireSpawner(final UUID spawnerId) {
        SpawnerInstance spawner = spawners.get(Objects.requireNonNull(spawnerId, "spawnerId"));
        if (spawner == null) {
            throw new IllegalArgumentException("Unknown spawner ID: " + spawnerId);
        }
        return spawner;
    }
}
