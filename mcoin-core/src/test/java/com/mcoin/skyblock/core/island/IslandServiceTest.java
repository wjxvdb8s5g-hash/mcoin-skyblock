package com.mcoin.skyblock.core.island;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class IslandServiceTest {
    @Test
    void managesBiomeHomeExpansionLevelAndCoopState() {
        IslandService service = new IslandService();
        UUID owner = UUID.randomUUID();
        UUID coop = UUID.randomUUID();

        IslandProfile island = service.createIsland(owner, "PLAINS");
        service.setBiome(island.getIslandId(), "DESERT");
        service.setHomeWarp(island.getIslandId(), "main-home");
        service.expandIsland(island.getIslandId(), 50);
        service.upgradeIslandLevel(island.getIslandId(), 2);
        service.addCoopMember(island.getIslandId(), coop);

        assertEquals("DESERT", island.getBiome());
        assertEquals("main-home", island.getHomeWarp());
        assertEquals(150, island.getSize());
        assertEquals(3, island.getLevel());
        assertEquals(IslandMemberRole.COOP, island.getMembers().get(coop));
        assertTrue(service.findByOwner(owner).isPresent());
    }

    @Test
    void rejectsDuplicateOwners() {
        IslandService service = new IslandService();
        UUID owner = UUID.randomUUID();
        service.createIsland(owner, "PLAINS");

        assertThrows(IllegalArgumentException.class, () -> service.createIsland(owner, "FOREST"));
    }

    @Test
    void snapshotReturnsDetachedIslandProfiles() {
        IslandService service = new IslandService();
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        IslandProfile island = service.createIsland(owner, "PLAINS");
        service.addCoopMember(island.getIslandId(), member);

        IslandProfile snapshotProfile = service.snapshot().get(island.getIslandId());
        snapshotProfile.setBiome("JUNGLE");
        snapshotProfile.removeMember(member);

        IslandProfile liveProfile = service.findByIslandId(island.getIslandId()).get();
        assertEquals("PLAINS", liveProfile.getBiome());
        assertEquals(IslandMemberRole.COOP, liveProfile.getMembers().get(member));
    }
}
