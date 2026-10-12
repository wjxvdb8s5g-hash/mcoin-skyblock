package com.mcoin.skyblock.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class WarpService {
    private final Map<String, WarpPoint> warps = new LinkedHashMap<String, WarpPoint>();

    public WarpService() {
        registerDefaults();
    }

    private void registerDefaults() {
        setWarp(new WarpPoint("spawn", "world", 0.0, 64.0, 0.0, null));
        setWarp(new WarpPoint("shop", "world", 100.0, 64.0, 0.0, null));
        setWarp(new WarpPoint("bank", "world", 150.0, 64.0, 0.0, null));
        setWarp(new WarpPoint("pvp", "world_nether", 0.0, 64.0, 0.0, null));
    }

    public synchronized void setWarp(final WarpPoint warp) {
        Objects.requireNonNull(warp, "warp");
        warps.put(warp.getName().toLowerCase(), warp);
    }

    public synchronized Optional<WarpPoint> getWarp(final String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(warps.get(name.toLowerCase()));
    }

    public synchronized boolean deleteWarp(final String name) {
        if (name == null) return false;
        return warps.remove(name.toLowerCase()) != null;
    }

    public synchronized List<WarpPoint> listWarps() {
        return Collections.unmodifiableList(new ArrayList<WarpPoint>(warps.values()));
    }
}
