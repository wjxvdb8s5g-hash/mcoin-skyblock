package com.mcoin.skyblock.core.plugin;

import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class PluginDescriptor {
    private final String name;
    private final String displayName;
    private final String description;
    private final String minimumMinecraftVersion;
    private final String maximumMinecraftVersion;
    private final Set<DatabaseMode> databaseModes;
    private final Set<FeatureArea> featureAreas;
    private final List<String> commands;
    private final List<String> capabilities;

    private PluginDescriptor(final Builder builder) {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.minimumMinecraftVersion = builder.minimumMinecraftVersion;
        this.maximumMinecraftVersion = builder.maximumMinecraftVersion;
        this.databaseModes = Collections.unmodifiableSet(EnumSet.copyOf(builder.databaseModes));
        this.featureAreas = Collections.unmodifiableSet(EnumSet.copyOf(builder.featureAreas));
        this.commands = Collections.unmodifiableList(new ArrayList<String>(builder.commands));
        this.capabilities = Collections.unmodifiableList(new ArrayList<String>(builder.capabilities));
    }

    public static Builder builder(final String name, final String displayName) {
        return new Builder(name, displayName);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getMinimumMinecraftVersion() {
        return minimumMinecraftVersion;
    }

    public String getMaximumMinecraftVersion() {
        return maximumMinecraftVersion;
    }

    public Set<DatabaseMode> getDatabaseModes() {
        return databaseModes;
    }

    public Set<FeatureArea> getFeatureAreas() {
        return featureAreas;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public static final class Builder {
        private final String name;
        private final String displayName;
        private String description = "";
        private String minimumMinecraftVersion = "1.8.x";
        private String maximumMinecraftVersion = "1.21.x";
        private EnumSet<DatabaseMode> databaseModes = EnumSet.of(DatabaseMode.SQLITE, DatabaseMode.MYSQL);
        private EnumSet<FeatureArea> featureAreas = EnumSet.noneOf(FeatureArea.class);
        private List<String> commands = Collections.emptyList();
        private List<String> capabilities = Collections.emptyList();

        private Builder(final String name, final String displayName) {
            this.name = requireText(name, "name");
            this.displayName = requireText(displayName, "displayName");
        }

        public Builder description(final String description) {
            this.description = requireText(description, "description");
            return this;
        }

        public Builder minecraftRange(final String minimumMinecraftVersion,
                                      final String maximumMinecraftVersion) {
            this.minimumMinecraftVersion = requireText(minimumMinecraftVersion, "minimumMinecraftVersion");
            this.maximumMinecraftVersion = requireText(maximumMinecraftVersion, "maximumMinecraftVersion");
            return this;
        }

        public Builder databaseModes(final Set<DatabaseMode> databaseModes) {
            this.databaseModes = EnumSet.copyOf(Objects.requireNonNull(databaseModes, "databaseModes"));
            return this;
        }

        public Builder featureAreas(final Set<FeatureArea> featureAreas) {
            this.featureAreas = EnumSet.copyOf(Objects.requireNonNull(featureAreas, "featureAreas"));
            return this;
        }

        public Builder commands(final List<String> commands) {
            this.commands = copyOf(commands, "commands");
            return this;
        }

        public Builder capabilities(final List<String> capabilities) {
            this.capabilities = copyOf(capabilities, "capabilities");
            return this;
        }

        public PluginDescriptor build() {
            if (featureAreas.isEmpty()) {
                throw new IllegalStateException("featureAreas must not be empty");
            }
            return new PluginDescriptor(this);
        }

        private static String requireText(final String value, final String name) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(name + " must not be blank");
            }
            return value;
        }

        private static List<String> copyOf(final List<String> values, final String name) {
            Objects.requireNonNull(values, name);
            final List<String> copy = new ArrayList<String>(values.size());
            for (String value : values) {
                copy.add(requireText(value, name));
            }
            return copy;
        }
    }
}
