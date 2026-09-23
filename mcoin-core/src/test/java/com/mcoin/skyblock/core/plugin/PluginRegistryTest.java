package com.mcoin.skyblock.core.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mcoin.skyblock.core.storage.DatabaseMode;
import java.util.Arrays;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class PluginRegistryTest {
    @Test
    void registersUniquePluginsAndPreservesMetadata() {
        PluginRegistry registry = new PluginRegistry();
        PluginDescriptor descriptor = PluginDescriptor.builder("MCoinExample", "MCoin Example")
            .description("Example plugin")
            .featureAreas(EnumSet.of(FeatureArea.ECONOMY))
            .databaseModes(EnumSet.of(DatabaseMode.MYSQL, DatabaseMode.SQLITE))
            .commands(Arrays.asList("example"))
            .capabilities(Arrays.asList("example-capability"))
            .build();

        registry.register(descriptor);

        assertEquals(1, registry.list().size());
        assertTrue(registry.find("MCoinExample").isPresent());
        assertTrue(registry.find("MCoinExample").get().getDatabaseModes().contains(DatabaseMode.MYSQL));
    }

    @Test
    void rejectsDuplicatePluginNames() {
        PluginRegistry registry = new PluginRegistry();
        PluginDescriptor descriptor = PluginDescriptor.builder("MCoinExample", "MCoin Example")
            .description("Example plugin")
            .featureAreas(EnumSet.of(FeatureArea.CORE))
            .build();

        registry.register(descriptor);

        assertThrows(IllegalArgumentException.class, () -> registry.register(descriptor));
    }
}
