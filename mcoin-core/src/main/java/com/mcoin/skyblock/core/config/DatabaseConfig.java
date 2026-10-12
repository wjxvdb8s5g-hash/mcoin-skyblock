package com.mcoin.skyblock.core.config;

import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Objects;

public final class DatabaseConfig {
    private final DatabaseMode mode;
    private final String sqlitePath;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int poolSize;

    public DatabaseConfig(final DatabaseMode mode,
                          final String sqlitePath,
                          final String host,
                          final int port,
                          final String database,
                          final String username,
                          final String password,
                          final int poolSize) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.sqlitePath = Objects.requireNonNull(sqlitePath, "sqlitePath");
        this.host = Objects.requireNonNull(host, "host");
        this.port = port;
        this.database = Objects.requireNonNull(database, "database");
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.poolSize = poolSize <= 0 ? 10 : poolSize;
    }

    public static DatabaseConfig sqlite(final String path) {
        return new DatabaseConfig(DatabaseMode.SQLITE, path, "localhost", 3306, "mcoin", "root", "", 10);
    }

    public static DatabaseConfig mysql(final String host, final int port, final String database, final String username, final String password) {
        return new DatabaseConfig(DatabaseMode.MYSQL, "mcoin.db", host, port, database, username, password, 10);
    }

    public static DatabaseConfig defaultSqlite() {
        return sqlite("plugins/MCoinSkyblock/mcoin.db");
    }

    public DatabaseMode getMode() {
        return mode;
    }

    public String getSqlitePath() {
        return sqlitePath;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
