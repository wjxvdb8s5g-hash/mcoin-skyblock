package com.mcoin.skyblock.core.config;

import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Properties;

public final class MCoinConfig {
    private final String serverName;
    private final String language;
    private final DatabaseConfig database;
    private final EconomyConfig economy;
    private final IslandConfig island;

    public MCoinConfig(final String serverName,
                       final String language,
                       final DatabaseConfig database,
                       final EconomyConfig economy,
                       final IslandConfig island) {
        this.serverName = Objects.requireNonNull(serverName, "serverName");
        this.language = Objects.requireNonNull(language, "language");
        this.database = Objects.requireNonNull(database, "database");
        this.economy = Objects.requireNonNull(economy, "economy");
        this.island = Objects.requireNonNull(island, "island");
    }

    public static MCoinConfig defaultConfig() {
        return new MCoinConfig(
            "MCoin Skyblock Server",
            "en",
            DatabaseConfig.defaultSqlite(),
            EconomyConfig.defaultConfig(),
            IslandConfig.defaultConfig()
        );
    }

    public static MCoinConfig fromProperties(final Properties props) {
        Objects.requireNonNull(props, "props");

        String serverName = props.getProperty("server.name", "MCoin Skyblock Server");
        String language = props.getProperty("server.language", "en");

        DatabaseMode dbMode = DatabaseMode.valueOf(props.getProperty("database.mode", "SQLITE").toUpperCase());
        String dbSqlitePath = props.getProperty("database.sqlite.path", "plugins/MCoinSkyblock/mcoin.db");
        String dbHost = props.getProperty("database.host", "localhost");
        int dbPort = Integer.parseInt(props.getProperty("database.port", "3306"));
        String dbName = props.getProperty("database.name", "mcoin");
        String dbUser = props.getProperty("database.username", "root");
        String dbPass = props.getProperty("database.password", "");
        int dbPool = Integer.parseInt(props.getProperty("database.pool-size", "10"));
        DatabaseConfig database = new DatabaseConfig(dbMode, dbSqlitePath, dbHost, dbPort, dbName, dbUser, dbPass, dbPool);

        BigDecimal startingMcoin = new BigDecimal(props.getProperty("economy.starting.mcoin", "100.00"));
        BigDecimal startingLidya = new BigDecimal(props.getProperty("economy.starting.lidya", "1000.00"));
        BigDecimal taxRate = new BigDecimal(props.getProperty("economy.transfer.tax-rate", "0.00"));
        BigDecimal interestRate = new BigDecimal(props.getProperty("economy.bank.interest-rate", "0.01"));
        EconomyConfig economy = new EconomyConfig(startingMcoin, startingLidya, taxRate, interestRate);

        String defaultBiome = props.getProperty("island.default.biome", "PLAINS");
        int defaultSize = Integer.parseInt(props.getProperty("island.default.size", "100"));
        int maxSize = Integer.parseInt(props.getProperty("island.max.size", "500"));
        int maxCoop = Integer.parseInt(props.getProperty("island.max.coop-members", "8"));
        BigDecimal upgradeCost = new BigDecimal(props.getProperty("island.upgrade.cost-per-level", "500.00"));
        BigDecimal expansionCost = new BigDecimal(props.getProperty("island.expansion.cost-per-block", "10.00"));
        BigDecimal biomeCost = new BigDecimal(props.getProperty("island.biome-change.cost", "250.00"));
        IslandConfig island = new IslandConfig(defaultBiome, defaultSize, maxSize, maxCoop, upgradeCost, expansionCost, biomeCost);

        return new MCoinConfig(serverName, language, database, economy, island);
    }

    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("server.name", serverName);
        props.setProperty("server.language", language);

        props.setProperty("database.mode", database.getMode().name());
        props.setProperty("database.sqlite.path", database.getSqlitePath());
        props.setProperty("database.host", database.getHost());
        props.setProperty("database.port", String.valueOf(database.getPort()));
        props.setProperty("database.name", database.getDatabase());
        props.setProperty("database.username", database.getUsername());
        props.setProperty("database.password", database.getPassword());
        props.setProperty("database.pool-size", String.valueOf(database.getPoolSize()));

        props.setProperty("economy.starting.mcoin", economy.getStartingMcoin().toPlainString());
        props.setProperty("economy.starting.lidya", economy.getStartingLidya().toPlainString());
        props.setProperty("economy.transfer.tax-rate", economy.getTransferTaxRate().toPlainString());
        props.setProperty("economy.bank.interest-rate", economy.getBankInterestRate().toPlainString());

        props.setProperty("island.default.biome", island.getDefaultBiome());
        props.setProperty("island.default.size", String.valueOf(island.getDefaultSize()));
        props.setProperty("island.max.size", String.valueOf(island.getMaxIslandSize()));
        props.setProperty("island.max.coop-members", String.valueOf(island.getMaxCoopMembers()));
        props.setProperty("island.upgrade.cost-per-level", island.getUpgradeCostPerLevel().toPlainString());
        props.setProperty("island.expansion.cost-per-block", island.getExpansionCostPerBlock().toPlainString());
        props.setProperty("island.biome-change.cost", island.getBiomeChangeCost().toPlainString());
        return props;
    }

    public static MCoinConfig load(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (!file.exists()) {
            MCoinConfig defaults = defaultConfig();
            defaults.save(file);
            return defaults;
        }
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        }
        return fromProperties(props);
    }

    public void save(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStream out = new FileOutputStream(file)) {
            toProperties().store(out, "MCoin Skyblock Configuration");
        }
    }

    public String getServerName() {
        return serverName;
    }

    public String getLanguage() {
        return language;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public DatabaseConfig getDatabaseConfig() {
        return database;
    }

    public EconomyConfig getEconomy() {
        return economy;
    }

    public EconomyConfig getEconomyConfig() {
        return economy;
    }

    public IslandConfig getIsland() {
        return island;
    }

    public IslandConfig getIslandConfig() {
        return island;
    }
}
