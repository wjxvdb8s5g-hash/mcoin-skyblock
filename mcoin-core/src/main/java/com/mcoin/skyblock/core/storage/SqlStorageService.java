package com.mcoin.skyblock.core.storage;

import com.mcoin.skyblock.core.config.DatabaseConfig;
import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.island.IslandMemberRole;
import com.mcoin.skyblock.core.island.IslandProfile;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SqlStorageService implements StorageService {
    private final DatabaseConfig config;
    private final String jdbcUrl;
    private Connection connection;

    public SqlStorageService(final DatabaseConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        if (config.getMode() == DatabaseMode.SQLITE) {
            this.jdbcUrl = "jdbc:sqlite:" + config.getSqlitePath();
        } else {
            this.jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8",
                config.getHost(), config.getPort(), config.getDatabase());
        }
    }

    public SqlStorageService(final String jdbcUrl) {
        this.config = DatabaseConfig.sqlite(jdbcUrl.replace("jdbc:sqlite:", ""));
        this.jdbcUrl = Objects.requireNonNull(jdbcUrl, "jdbcUrl");
    }

    @Override
    public synchronized void init() {
        try {
            ensureConnection();
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQL storage: " + e.getMessage(), e);
        }
    }

    private synchronized Connection ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (config.getMode() == DatabaseMode.SQLITE && !":memory:".equals(config.getSqlitePath())) {
                File dbFile = new File(config.getSqlitePath());
                File parent = dbFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
            }
            if (config.getMode() == DatabaseMode.SQLITE) {
                connection = DriverManager.getConnection(jdbcUrl);
            } else {
                connection = DriverManager.getConnection(jdbcUrl, config.getUsername(), config.getPassword());
            }
            connection.setAutoCommit(true);
        }
        return connection;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mcoin_accounts ("
                + "player_id VARCHAR(36) PRIMARY KEY, "
                + "mcoin DECIMAL(19, 4) NOT NULL, "
                + "lidya DECIMAL(19, 4) NOT NULL"
                + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mcoin_bank_accounts ("
                + "player_id VARCHAR(36) PRIMARY KEY, "
                + "mcoin DECIMAL(19, 4) NOT NULL, "
                + "lidya DECIMAL(19, 4) NOT NULL"
                + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mcoin_islands ("
                + "island_id VARCHAR(36) PRIMARY KEY, "
                + "owner_id VARCHAR(36) NOT NULL, "
                + "biome VARCHAR(64) NOT NULL, "
                + "home_warp VARCHAR(128) NOT NULL, "
                + "size INT NOT NULL, "
                + "level INT NOT NULL"
                + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mcoin_island_members ("
                + "island_id VARCHAR(36) NOT NULL, "
                + "player_id VARCHAR(36) NOT NULL, "
                + "role VARCHAR(32) NOT NULL, "
                + "PRIMARY KEY (island_id, player_id)"
                + ")");
        }
    }

    @Override
    public synchronized void saveAccount(final CurrencyAccount account) {
        Objects.requireNonNull(account, "account");
        saveAccountInternal("mcoin_accounts", account);
    }

    @Override
    public synchronized Optional<CurrencyAccount> loadAccount(final UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return loadAccountInternal("mcoin_accounts", playerId);
    }

    @Override
    public synchronized Map<UUID, CurrencyAccount> loadAllAccounts() {
        return loadAllAccountsInternal("mcoin_accounts");
    }

    @Override
    public synchronized void deleteAccount(final UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM mcoin_accounts WHERE player_id = ?")) {
                ps.setString(1, playerId.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete account: " + playerId, e);
        }
    }

    @Override
    public synchronized void saveBankAccount(final CurrencyAccount account) {
        Objects.requireNonNull(account, "account");
        saveAccountInternal("mcoin_bank_accounts", account);
    }

    @Override
    public synchronized Optional<CurrencyAccount> loadBankAccount(final UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return loadAccountInternal("mcoin_bank_accounts", playerId);
    }

    @Override
    public synchronized Map<UUID, CurrencyAccount> loadAllBankAccounts() {
        return loadAllAccountsInternal("mcoin_bank_accounts");
    }

    private void saveAccountInternal(final String table, final CurrencyAccount account) {
        try {
            ensureConnection();
            String sqlDelete = "DELETE FROM " + table + " WHERE player_id = ?";
            String sqlInsert = "INSERT INTO " + table + " (player_id, mcoin, lidya) VALUES (?, ?, ?)";
            try (PreparedStatement psDel = connection.prepareStatement(sqlDelete)) {
                psDel.setString(1, account.getPlayerId().toString());
                psDel.executeUpdate();
            }
            try (PreparedStatement psIns = connection.prepareStatement(sqlInsert)) {
                psIns.setString(1, account.getPlayerId().toString());
                psIns.setBigDecimal(2, account.getBalance(CurrencyType.MCOIN));
                psIns.setBigDecimal(3, account.getBalance(CurrencyType.LIDYA));
                psIns.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save account to " + table + ": " + account.getPlayerId(), e);
        }
    }

    private Optional<CurrencyAccount> loadAccountInternal(final String table, final UUID playerId) {
        try {
            ensureConnection();
            String sql = "SELECT mcoin, lidya FROM " + table + " WHERE player_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, playerId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal mcoin = rs.getBigDecimal("mcoin");
                        BigDecimal lidya = rs.getBigDecimal("lidya");
                        CurrencyAccount account = new CurrencyAccount(playerId);
                        if (mcoin != null && mcoin.compareTo(BigDecimal.ZERO) > 0) {
                            account.deposit(CurrencyType.MCOIN, mcoin);
                        }
                        if (lidya != null && lidya.compareTo(BigDecimal.ZERO) > 0) {
                            account.deposit(CurrencyType.LIDYA, lidya);
                        }
                        return Optional.of(account);
                    }
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load account from " + table + ": " + playerId, e);
        }
    }

    private Map<UUID, CurrencyAccount> loadAllAccountsInternal(final String table) {
        Map<UUID, CurrencyAccount> result = new LinkedHashMap<UUID, CurrencyAccount>();
        try {
            ensureConnection();
            String sql = "SELECT player_id, mcoin, lidya FROM " + table;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    UUID playerId = UUID.fromString(rs.getString("player_id"));
                    BigDecimal mcoin = rs.getBigDecimal("mcoin");
                    BigDecimal lidya = rs.getBigDecimal("lidya");
                    CurrencyAccount account = new CurrencyAccount(playerId);
                    if (mcoin != null && mcoin.compareTo(BigDecimal.ZERO) > 0) {
                        account.deposit(CurrencyType.MCOIN, mcoin);
                    }
                    if (lidya != null && lidya.compareTo(BigDecimal.ZERO) > 0) {
                        account.deposit(CurrencyType.LIDYA, lidya);
                    }
                    result.put(playerId, account);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load all accounts from " + table, e);
        }
    }

    @Override
    public synchronized void saveIsland(final IslandProfile island) {
        Objects.requireNonNull(island, "island");
        try {
            ensureConnection();
            connection.setAutoCommit(false);
            try {
                // Delete old island record & members
                try (PreparedStatement ps = connection.prepareStatement("DELETE FROM mcoin_islands WHERE island_id = ?")) {
                    ps.setString(1, island.getIslandId().toString());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement("DELETE FROM mcoin_island_members WHERE island_id = ?")) {
                    ps.setString(1, island.getIslandId().toString());
                    ps.executeUpdate();
                }

                // Insert island
                try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO mcoin_islands (island_id, owner_id, biome, home_warp, size, level) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, island.getIslandId().toString());
                    ps.setString(2, island.getOwnerId().toString());
                    ps.setString(3, island.getBiome());
                    ps.setString(4, island.getHomeWarp());
                    ps.setInt(5, island.getSize());
                    ps.setInt(6, island.getLevel());
                    ps.executeUpdate();
                }

                // Insert members
                try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO mcoin_island_members (island_id, player_id, role) VALUES (?, ?, ?)")) {
                    for (Map.Entry<UUID, IslandMemberRole> entry : island.getMembers().entrySet()) {
                        ps.setString(1, island.getIslandId().toString());
                        ps.setString(2, entry.getKey().toString());
                        ps.setString(3, entry.getValue().name());
                        ps.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save island: " + island.getIslandId(), e);
        }
    }

    @Override
    public synchronized Optional<IslandProfile> loadIsland(final UUID islandId) {
        Objects.requireNonNull(islandId, "islandId");
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM mcoin_islands WHERE island_id = ?")) {
                ps.setString(1, islandId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(buildIsland(rs));
                    }
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load island: " + islandId, e);
        }
    }

    @Override
    public synchronized Optional<IslandProfile> loadIslandByOwner(final UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM mcoin_islands WHERE owner_id = ?")) {
                ps.setString(1, ownerId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(buildIsland(rs));
                    }
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load island for owner: " + ownerId, e);
        }
    }

    @Override
    public synchronized Map<UUID, IslandProfile> loadAllIslands() {
        Map<UUID, IslandProfile> result = new LinkedHashMap<UUID, IslandProfile>();
        try {
            ensureConnection();
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mcoin_islands")) {
                while (rs.next()) {
                    IslandProfile profile = buildIsland(rs);
                    result.put(profile.getIslandId(), profile);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load all islands", e);
        }
    }

    @Override
    public synchronized void deleteIsland(final UUID islandId) {
        Objects.requireNonNull(islandId, "islandId");
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM mcoin_islands WHERE island_id = ?")) {
                ps.setString(1, islandId.toString());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM mcoin_island_members WHERE island_id = ?")) {
                ps.setString(1, islandId.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete island: " + islandId, e);
        }
    }

    private IslandProfile buildIsland(final ResultSet rs) throws SQLException {
        UUID islandId = UUID.fromString(rs.getString("island_id"));
        UUID ownerId = UUID.fromString(rs.getString("owner_id"));
        String biome = rs.getString("biome");
        String homeWarp = rs.getString("home_warp");
        int size = rs.getInt("size");
        int level = rs.getInt("level");

        IslandProfile profile = new IslandProfile(islandId, ownerId, biome);
        profile.setHomeWarp(homeWarp);
        profile.setSize(size);
        profile.setLevel(level);

        // Load members
        try (PreparedStatement psMem = connection.prepareStatement(
            "SELECT player_id, role FROM mcoin_island_members WHERE island_id = ?")) {
            psMem.setString(1, islandId.toString());
            try (ResultSet rsMem = psMem.executeQuery()) {
                while (rsMem.next()) {
                    UUID memberId = UUID.fromString(rsMem.getString("player_id"));
                    IslandMemberRole role = IslandMemberRole.valueOf(rsMem.getString("role"));
                    profile.setMemberRole(memberId, role);
                }
            }
        }
        return profile;
    }

    @Override
    public synchronized void flush() {
        // No-op for direct SQL writes
    }

    @Override
    public synchronized void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            } finally {
                connection = null;
            }
        }
    }
}
