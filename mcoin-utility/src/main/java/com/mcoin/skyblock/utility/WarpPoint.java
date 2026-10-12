package com.mcoin.skyblock.utility;

import java.util.Objects;

public final class WarpPoint {
    private final String name;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final String permission;

    public WarpPoint(final String name,
                     final String world,
                     final double x,
                     final double y,
                     final double z,
                     final String permission) {
        this.name = Objects.requireNonNull(name, "name");
        this.world = Objects.requireNonNull(world, "world");
        this.x = x;
        this.y = y;
        this.z = z;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getPermission() {
        return permission;
    }
}
