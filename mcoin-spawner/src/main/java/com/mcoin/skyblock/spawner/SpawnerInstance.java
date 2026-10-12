package com.mcoin.skyblock.spawner;

import java.util.Objects;
import java.util.UUID;

public final class SpawnerInstance {
    private final UUID spawnerId;
    private final UUID ownerId;
    private final UUID islandId;
    private final SpawnerType type;
    private int tier;
    private int stackCount;

    public SpawnerInstance(final UUID spawnerId,
                           final UUID ownerId,
                           final UUID islandId,
                           final SpawnerType type,
                           final int tier,
                           final int stackCount) {
        this.spawnerId = Objects.requireNonNull(spawnerId, "spawnerId");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        this.islandId = Objects.requireNonNull(islandId, "islandId");
        this.type = Objects.requireNonNull(type, "type");
        if (tier < 1 || tier > 5) {
            throw new IllegalArgumentException("Tier must be between 1 and 5");
        }
        if (stackCount < 1 || stackCount > 64) {
            throw new IllegalArgumentException("Stack count must be between 1 and 64");
        }
        this.tier = tier;
        this.stackCount = stackCount;
    }

    public UUID getSpawnerId() {
        return spawnerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getIslandId() {
        return islandId;
    }

    public SpawnerType getType() {
        return type;
    }

    public synchronized int getTier() {
        return tier;
    }

    public synchronized void setTier(final int tier) {
        if (tier < 1 || tier > 5) {
            throw new IllegalArgumentException("Tier must be between 1 and 5");
        }
        this.tier = tier;
    }

    public synchronized int getStackCount() {
        return stackCount;
    }

    public synchronized void setStackCount(final int stackCount) {
        if (stackCount < 1 || stackCount > 64) {
            throw new IllegalArgumentException("Stack count must be between 1 and 64");
        }
        this.stackCount = stackCount;
    }

    public synchronized double getSpawnRateMultiplier() {
        return 1.0 + ((tier - 1) * 0.25);
    }

    public synchronized double getDropMultiplier() {
        return 1.0 + ((tier - 1) * 0.50);
    }
}
