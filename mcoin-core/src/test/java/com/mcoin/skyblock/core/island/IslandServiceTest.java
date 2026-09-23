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
}
