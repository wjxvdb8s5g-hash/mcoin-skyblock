package com.mcoin.skyblock.core.storage;

import com.mcoin.skyblock.core.currency.CurrencyAccount;
import com.mcoin.skyblock.core.currency.CurrencyType;
import com.mcoin.skyblock.core.island.IslandMemberRole;
import com.mcoin.skyblock.core.island.IslandProfile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileStorageService implements StorageService {
    private final File dataDir;
    private final Map<UUID, CurrencyAccount> accounts = new LinkedHashMap<UUID, CurrencyAccount>();
    private final Map<UUID, CurrencyAccount> bankAccounts = new LinkedHashMap<UUID, CurrencyAccount>();
    private final Map<UUID, IslandProfile> islands = new LinkedHashMap<UUID, IslandProfile>();

    public FileStorageService(final File dataDir) {
        this.dataDir = Objects.requireNonNull(dataDir, "dataDir");
    }

    @Override
    public synchronized void init() {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        loadAll();
    }

    @Override
    public synchronized void saveAccount(final CurrencyAccount account) {
        accounts.put(account.getPlayerId(), CurrencyAccount.copyOf(account));
        flush();
    }

    @Override
    public synchronized Optional<CurrencyAccount> loadAccount(final UUID playerId) {
        CurrencyAccount account = accounts.get(playerId);
        return account == null ? Optional.empty() : Optional.of(CurrencyAccount.copyOf(account));
    }

    @Override
    public synchronized Map<UUID, CurrencyAccount> loadAllAccounts() {
        Map<UUID, CurrencyAccount> copy = new LinkedHashMap<UUID, CurrencyAccount>();
        for (Map.Entry<UUID, CurrencyAccount> entry : accounts.entrySet()) {
            copy.put(entry.getKey(), CurrencyAccount.copyOf(entry.getValue()));
        }
        return copy;
    }

    @Override
    public synchronized void deleteAccount(final UUID playerId) {
        accounts.remove(playerId);
        flush();
    }

    @Override
    public synchronized void saveBankAccount(final CurrencyAccount account) {
        bankAccounts.put(account.getPlayerId(), CurrencyAccount.copyOf(account));
        flush();
    }

    @Override
    public synchronized Optional<CurrencyAccount> loadBankAccount(final UUID playerId) {
        CurrencyAccount account = bankAccounts.get(playerId);
        return account == null ? Optional.empty() : Optional.of(CurrencyAccount.copyOf(account));
    }

    @Override
    public synchronized Map<UUID, CurrencyAccount> loadAllBankAccounts() {
        Map<UUID, CurrencyAccount> copy = new LinkedHashMap<UUID, CurrencyAccount>();
        for (Map.Entry<UUID, CurrencyAccount> entry : bankAccounts.entrySet()) {
            copy.put(entry.getKey(), CurrencyAccount.copyOf(entry.getValue()));
        }
        return copy;
    }

    @Override
    public synchronized void saveIsland(final IslandProfile island) {
        islands.put(island.getIslandId(), IslandProfile.copyOf(island));
        flush();
    }

    @Override
    public synchronized Optional<IslandProfile> loadIsland(final UUID islandId) {
        IslandProfile island = islands.get(islandId);
        return island == null ? Optional.empty() : Optional.of(IslandProfile.copyOf(island));
    }

    @Override
    public synchronized Optional<IslandProfile> loadIslandByOwner(final UUID ownerId) {
        for (IslandProfile island : islands.values()) {
            if (island.getOwnerId().equals(ownerId)) {
                return Optional.of(IslandProfile.copyOf(island));
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Map<UUID, IslandProfile> loadAllIslands() {
        Map<UUID, IslandProfile> copy = new LinkedHashMap<UUID, IslandProfile>();
        for (Map.Entry<UUID, IslandProfile> entry : islands.entrySet()) {
            copy.put(entry.getKey(), IslandProfile.copyOf(entry.getValue()));
        }
        return copy;
    }

    @Override
    public synchronized void deleteIsland(final UUID islandId) {
        islands.remove(islandId);
        flush();
    }

    @Override
    public synchronized void flush() {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        saveAccountsFile(new File(dataDir, "accounts.csv"), accounts);
        saveAccountsFile(new File(dataDir, "bank_accounts.csv"), bankAccounts);
        saveIslandsFile(new File(dataDir, "islands.csv"), islands);
    }

    private void loadAll() {
        loadAccountsFile(new File(dataDir, "accounts.csv"), accounts);
        loadAccountsFile(new File(dataDir, "bank_accounts.csv"), bankAccounts);
        loadIslandsFile(new File(dataDir, "islands.csv"), islands);
    }

    private void saveAccountsFile(final File file, final Map<UUID, CurrencyAccount> map) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (CurrencyAccount acc : map.values()) {
                writer.write(acc.getPlayerId().toString() + "," +
                    acc.getBalance(CurrencyType.MCOIN).toPlainString() + "," +
                    acc.getBalance(CurrencyType.LIDYA).toPlainString() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save accounts to " + file, e);
        }
    }

    private void loadAccountsFile(final File file, final Map<UUID, CurrencyAccount> map) {
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    UUID id = UUID.fromString(parts[0]);
                    CurrencyAccount acc = new CurrencyAccount(id);
                    BigDecimal mcoin = new BigDecimal(parts[1]);
                    BigDecimal lidya = new BigDecimal(parts[2]);
                    if (mcoin.compareTo(BigDecimal.ZERO) > 0) acc.deposit(CurrencyType.MCOIN, mcoin);
                    if (lidya.compareTo(BigDecimal.ZERO) > 0) acc.deposit(CurrencyType.LIDYA, lidya);
                    map.put(id, acc);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load accounts from " + file, e);
        }
    }

    private void saveIslandsFile(final File file, final Map<UUID, IslandProfile> map) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (IslandProfile island : map.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(island.getIslandId()).append(";")
                  .append(island.getOwnerId()).append(";")
                  .append(island.getBiome()).append(";")
                  .append(island.getHomeWarp()).append(";")
                  .append(island.getSize()).append(";")
                  .append(island.getLevel()).append(";");
                boolean first = true;
                for (Map.Entry<UUID, IslandMemberRole> mem : island.getMembers().entrySet()) {
                    if (!first) sb.append(",");
                    sb.append(mem.getKey().toString()).append(":").append(mem.getValue().name());
                    first = false;
                }
                sb.append("\n");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save islands to " + file, e);
        }
    }

    private void loadIslandsFile(final File file, final Map<UUID, IslandProfile> map) {
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 6) {
                    UUID islandId = UUID.fromString(parts[0]);
                    UUID ownerId = UUID.fromString(parts[1]);
                    String biome = parts[2];
                    String homeWarp = parts[3];
                    int size = Integer.parseInt(parts[4]);
                    int level = Integer.parseInt(parts[5]);

                    IslandProfile profile = new IslandProfile(islandId, ownerId, biome);
                    profile.setHomeWarp(homeWarp);
                    profile.setSize(size);
                    profile.setLevel(level);

                    if (parts.length >= 7 && !parts[6].isEmpty()) {
                        String[] members = parts[6].split(",");
                        for (String mem : members) {
                            String[] kv = mem.split(":");
                            if (kv.length == 2) {
                                profile.setMemberRole(UUID.fromString(kv[0]), IslandMemberRole.valueOf(kv[1]));
                            }
                        }
                    }
                    map.put(islandId, profile);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load islands from " + file, e);
        }
    }

    @Override
    public synchronized void close() {
        flush();
    }
}
