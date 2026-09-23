package com.mcoin.skyblock.core.plugin;

import java.util.Objects;

public abstract class AbstractFeaturePlugin implements FeaturePlugin {
    private final PluginDescriptor descriptor;

    protected AbstractFeaturePlugin(final PluginDescriptor descriptor) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
    }

    @Override
    public final PluginDescriptor descriptor() {
        return this.descriptor;
    }
}
