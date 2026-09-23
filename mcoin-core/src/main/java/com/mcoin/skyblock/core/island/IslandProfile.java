package com.mcoin.skyblock.core.island;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class IslandProfile {
    private final UUID islandId;
    private final UUID ownerId;
    private final Map<UUID, IslandMemberRole> members = new LinkedHashMap<UUID, IslandMemberRole>();
    private String biome;
    private String homeWarp;
    private int size;
    private int level;

    public IslandProfile(final UUID islandId, final UUID ownerId, final String biome) {
        this.islandId = Objects.requireNonNull(islandId, "islandId");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        this.biome = requireText(biome, "biome");
        this.homeWarp = "island-home";
        this.size = 100;
        this.level = 1;
        this.members.put(ownerId, IslandMemberRole.OWNER);
    }

    public UUID getIslandId() {
        return islandId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public synchronized Map<UUID, IslandMemberRole> getMembers() {
        return Collections.unmodifiableMap(new LinkedHashMap<UUID, IslandMemberRole>(members));
    }

    public synchronized String getBiome() {
        return biome;
    }

    public synchronized void setBiome(final String biome) {
        this.biome = requireText(biome, "biome");
    }

    public synchronized String getHomeWarp() {
        return homeWarp;
    }

    public synchronized void setHomeWarp(final String homeWarp) {
        this.homeWarp = requireText(homeWarp, "homeWarp");
    }

    public synchronized int getSize() {
        return size;
    }

    public synchronized void expand(final int blocks) {
        if (blocks <= 0) {
            throw new IllegalArgumentException("blocks must be positive");
        }
        this.size += blocks;
    }

    public synchronized int getLevel() {
        return level;
    }

    public synchronized void upgradeLevel(final int levels) {
        if (levels <= 0) {
            throw new IllegalArgumentException("levels must be positive");
        }
        this.level += levels;
    }

    public synchronized void setMemberRole(final UUID playerId, final IslandMemberRole role) {
        members.put(Objects.requireNonNull(playerId, "playerId"), Objects.requireNonNull(role, "role"));
    }

    public synchronized void removeMember(final UUID playerId) {
        if (ownerId.equals(playerId)) {
            throw new IllegalArgumentException("owner cannot be removed");
        }
        members.remove(Objects.requireNonNull(playerId, "playerId"));
    }

    private static String requireText(final String value, final String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
