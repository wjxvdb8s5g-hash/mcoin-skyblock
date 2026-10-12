# mcoin-skyblock

Modular MCoin Skyblock package foundation inspired by premium Turkish Skyblock server stacks.

## Included modules

- `MCoinCore` - shared API, configuration, database mode metadata and module registry
- `MCoinEconomy` - MCOIN premium currency and Lidya in-game economy metadata plus bank, server shop and player trade services
- `MCoinIsland` - island creation, co-op, biome, home and upgrade metadata
- `MCoinSpawner` - spawner catalog, stacking, upgrades and drop metadata
- `MCoinProgression` - VIP rank, prestige, skill, mastery, achievement and XP metadata
- `MCoinFarming` - farming, crops, minions and auto-harvest metadata
- `MCoinQuest` - daily, weekly, bounty and challenge metadata
- `MCoinPet` - pet, companion and pet marketplace metadata
- `MCoinLeaderboard` - ranking and statistics metadata
- `MCoinNPC` - NPC, dialogue and quest giver metadata
- `MCoinEvents` - event, seasonal and boss metadata
- `MCoinCosmetics` - particles, emotes and visual customization metadata
- `MCoinUtility` - home, warp, region, cleanup and admin metadata

## Build

```bash
mvn test
```

## Notes

This repository started empty. The current build now includes a tested, Java 8 compatible multi-module foundation with shared MCOIN/Lidya economy primitives, detached snapshot support, island management primitives, and concrete economy services for bank, server shop, and player trading flows.
