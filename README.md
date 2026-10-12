# mcoin-skyblock

Production-ready modular Skyblock suite inspired by Turkish Skyblock networks, featuring a dual-currency economy (MCOIN premium currency + Lidya in-game currency), island lifecycle management, mob spawners with stacking and tier upgrades, skills & VIP rank progression, daily/weekly/challenge quests, dynamic leaderboards, server warps, and multi-backend persistence (SQLite, MySQL, Flatfile).

---

## 📦 Package Modules

| Module | Description | In-Game Commands |
|---|---|---|
| **MCoinCore** | Central bootstrap (`MCoinSkyblockBootstrap`), unified configuration (`MCoinConfig`), storage engine abstraction, and command dispatcher. | `/mcoin`, `/mc`, `/mcoinadmin` |
| **MCoinEconomy** | Bank interest/deposits, server shop, player-to-player trading, and dual-currency transactions. | `/mcoin pay`, `/mcoin balance`, `/bank`, `/shop`, `/trade` |
| **MCoinIsland** | Island creation, visits, members/co-op, biome updates, border upgrades, and home teleportation. | `/island`, `/is` |
| **MCoinSpawner** | Island mob spawners (Pig, Cow, Zombie, Skeleton, Blaze, Iron Golem) with stacking (up to 64) and tier upgrades (1-5). | `/spawner`, `/spawners` |
| **MCoinProgression** | Skills leveling (Mining, Farming, Combat, Foraging, Fishing), prestige resets, and VIP rank upgrades (VIP, VIP+, MVP, MVP+). | `/skills`, `/skill`, `/rank`, `/ranks` |
| **MCoinQuest** | Daily, weekly, and challenge quests with automatic progression tracking and currency rewards. | `/quest`, `/quests` |
| **MCoinLeaderboard** | Top island levels and currency rankings (Lidya & MCOIN). | `/leaderboard`, `/top` |
| **MCoinUtility** | Warps and teleportation points with permissions. | `/warp`, `/warps` |
| **MCoinFarming** | Crop types, minion automation, and harvest rates. | — |
| **MCoinPet** | Companion pets and pet leveling metadata. | — |
| **MCoinNPC** | NPC interactions, dialogue trees, and quest givers. | — |
| **MCoinEvents** | Periodic server events, seasonal activities, and boss battles. | — |
| **MCoinCosmetics** | Visual trails, particle effects, and emotes. | — |

---

## ⚙️ Configuration (`config.properties`)

When `MCoinSkyblockBootstrap` starts for the first time, it generates a default `config.properties`:

```properties
# Storage Settings: SQLITE, MYSQL, or FILE
storage.type=SQLITE
storage.sqlite.file=mcoin_skyblock.db
storage.mysql.host=localhost
storage.mysql.port=3306
storage.mysql.database=skyblock
storage.mysql.user=root
storage.mysql.******
storage.file.directory=data

# Economy Settings
economy.starting.lidya=1000.00
economy.starting.mcoin=0.00
economy.bank.interestRate=0.02
economy.bank.maxDeposit=10000000.00

# Island Settings
island.default.size=50
island.max.members=8
island.max.level=100
```

---

## 🎮 Command & Permission Reference

| Command | Usage | Permission | Description |
|---|---|---|---|
| `/mcoin` | `/mcoin [balance\|pay <uuid> <amount>]` | `mcoin.currency.use` | Check balances or pay other players. |
| `/mcoinadmin` | `/mcoinadmin <reload\|save>` | `mcoin.admin` | Save data or reload plugin configuration. |
| `/bank` | `/bank [balance\|deposit <curr> <amt>\|withdraw <curr> <amt>]` | `mcoin.economy.bank` | Access bank vault, deposit, or withdraw. |
| `/shop` | `/shop [list\|buy <item> <qty>\|sell <item> <qty>]` | `mcoin.economy.shop` | Buy and sell server shop items. |
| `/trade` | `/trade <request\|offer\|confirm\|cancel>` | `mcoin.economy.trade` | Safe trading between players. |
| `/island` | `/island <create\|home\|sethome\|visit\|invite\|kick\|upgrade\|biome\|info>` | `mcoin.island.use` | Full island lifecycle commands. |
| `/spawner` | `/spawner <list\|buy <type>\|upgrade <id>\|stack <id> <amt>\|info <id>>` | `mcoin.spawner.use` | Buy and stack island spawners. |
| `/skills` | `/skills` | `mcoin.skills.use` | Display skill levels and XP progress. |
| `/rank` | `/rank [list\|buy <rank>]` | `mcoin.rank.use` | View VIP perks and upgrade rank using MCOIN. |
| `/quest` | `/quest [list\|claim <questId>]` | `mcoin.quest.use` | Track quest progress and claim rewards. |
| `/top` | `/top [islands\|lidya\|mcoin]` | `mcoin.leaderboard.use` | Show server leaderboards. |
| `/warp` | `/warp [list\|<name>]` | `mcoin.warp.use` | Warp to server landmarks. |

---

## 🛠️ Build & Packaging

Build the entire package with Java 8+ and Maven:

```bash
# Compile and run test suite across all 13 modules
mvn clean test

# Build all production JAR artifacts into their respective target/ directories
mvn clean package
```

---

## 🧪 Architecture & Lifecycle

`MCoinSkyblockBootstrap` controls the plugin suite lifecycle:
1. **Startup (`start()`):**
   - Loads or creates configuration (`MCoinConfig`).
   - Initializes storage engine via `StorageFactory` (`SQLITE`, `MYSQL`, or `FILE`).
   - Loads stored player accounts and island profiles into core services.
   - Registers all commands and module sub-components into the central `CommandDispatcher`.
2. **Runtime Operations (`save()`, `reload()`):**
   - Automatically synchronizes memory state with the underlying storage layer on demand or interval.
3. **Shutdown (`shutdown()`):**
   - Atomically flushes all modified accounts, islands, and transactions to storage.
   - Closes database connections cleanly.

