package com.mcoin.skyblock.core.island;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class IslandService {
    private final Map<UUID, IslandProfile> islands = new LinkedHashMap<UUID, IslandProfile>();
    private final Map<UUID, UUID> owners = new LinkedHashMap<UUID, UUID>();

    public synchronized IslandProfile createIsland(final UUID ownerId, final String biome) {
        final UUID safeOwnerId = Objects.requireNonNull(ownerId, "ownerId");
        if (owners.containsKey(safeOwnerId)) {
            throw new IllegalArgumentException("owner already has an island");
        }
        final IslandProfile profile = new IslandProfile(UUID.randomUUID(), safeOwnerId, biome);
        islands.put(profile.getIslandId(), profile);
        owners.put(safeOwnerId, profile.getIslandId());
        return profile;
    }

    public synchronized Optional<IslandProfile> findByIslandId(final UUID islandId) {
        return Optional.ofNullable(islands.get(islandId));
    }

    public synchronized Optional<IslandProfile> findByOwner(final UUID ownerId) {
        final UUID islandId = owners.get(ownerId);
        return islandId == null ? Optional.<IslandProfile>empty() : Optional.of(islands.get(islandId));
    }

    public synchronized void setBiome(final UUID islandId, final String biome) {
        requireIsland(islandId).setBiome(biome);
    }

    public synchronized void setHomeWarp(final UUID islandId, final String homeWarp) {
        requireIsland(islandId).setHomeWarp(homeWarp);
    }

    public synchronized void expandIsland(final UUID islandId, final int blocks) {
        requireIsland(islandId).expand(blocks);
    }

    public synchronized void upgradeIslandLevel(final UUID islandId, final int levels) {
        requireIsland(islandId).upgradeLevel(levels);
    }

    public synchronized void addCoopMember(final UUID islandId, final UUID playerId) {
        requireIsland(islandId).setMemberRole(playerId, IslandMemberRole.COOP);
    }

    public synchronized void removeMember(final UUID islandId, final UUID playerId) {
        requireIsland(islandId).removeMember(playerId);
    }

    public synchronized Map<UUID, IslandProfile> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<UUID, IslandProfile>(islands));
    }

    private synchronized IslandProfile requireIsland(final UUID islandId) {
        return findByIslandId(Objects.requireNonNull(islandId, "islandId"))
            .orElseThrow(() -> new IllegalArgumentException("Unknown island: " + islandId));
    }
}
