package com.mcoin.skyblock.core.storage;

import com.mcoin.skyblock.core.config.DatabaseConfig;
import java.io.File;
import java.util.Objects;

public final class StorageFactory {
    private StorageFactory() {
    }

    public static StorageService create(final DatabaseConfig config) {
        return create(config, null);
    }

    public static StorageService create(final DatabaseConfig config, final File dataFolder) {
        Objects.requireNonNull(config, "config");
        if (config.getMode() == DatabaseMode.SQLITE || config.getMode() == DatabaseMode.MYSQL) {
            if (config.getMode() == DatabaseMode.SQLITE && dataFolder != null && !config.getSqlitePath().startsWith("jdbc:")) {
                File dbFile = new File(dataFolder, config.getSqlitePath());
                DatabaseConfig adjusted = new DatabaseConfig(
                    config.getMode(),
                    dbFile.getAbsolutePath(),
                    config.getHost(),
                    config.getPort(),
                    config.getDatabase(),
                    config.getUsername(),
                    config.getPassword(),
                    config.getPoolSize()
                );
                return new SqlStorageService(adjusted);
            }
            return new SqlStorageService(config);
        }
        File dataDir = dataFolder != null ? new File(dataFolder, "data") : new File("plugins/MCoinSkyblock/data");
        return new FileStorageService(dataDir);
    }

    public static StorageService fileBased(final File dir) {
        return new FileStorageService(dir);
    }
}
