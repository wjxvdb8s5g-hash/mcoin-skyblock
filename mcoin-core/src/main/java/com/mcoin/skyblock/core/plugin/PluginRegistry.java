package com.mcoin.skyblock.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PluginRegistry {
    private final Map<String, PluginDescriptor> descriptors = new LinkedHashMap<String, PluginDescriptor>();

    public void register(final FeaturePlugin plugin) {
        register(Objects.requireNonNull(plugin, "plugin").descriptor());
    }

    public void register(final PluginDescriptor descriptor) {
        final PluginDescriptor safeDescriptor = Objects.requireNonNull(descriptor, "descriptor");
        if (descriptors.containsKey(safeDescriptor.getName())) {
            throw new IllegalArgumentException("Duplicate plugin: " + safeDescriptor.getName());
        }
        descriptors.put(safeDescriptor.getName(), safeDescriptor);
    }

    public Optional<PluginDescriptor> find(final String name) {
        return Optional.ofNullable(descriptors.get(name));
    }

    public List<PluginDescriptor> list() {
        return Collections.unmodifiableList(new ArrayList<PluginDescriptor>(descriptors.values()));
    }
}
