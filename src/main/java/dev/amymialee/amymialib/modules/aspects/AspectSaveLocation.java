package dev.amymialee.amymialib.modules.aspects;

import dev.amymialee.amymialib.modules.AMEnvironmentType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.function.Supplier;

public enum AspectSaveLocation {
    CONFIG(() -> AMEnvironmentType.getEnv().isServer, () -> FabricLoader.getInstance().getConfigDir().resolve("amymialib-config-" + AMEnvironmentType.getEnv().name().toLowerCase())),
    CLIENT(() -> AMEnvironmentType.getEnv().isClient, () -> FabricLoader.getInstance().getConfigDir().resolve("amymialib-client"));

    private final Supplier<Boolean> shouldSave;
    private final Supplier<Path> path;

    AspectSaveLocation(Supplier<Boolean> shouldSave, Supplier<Path> path) {
        this.shouldSave = shouldSave;
        this.path = path;
    }

    public boolean shouldSave() {
        return this.shouldSave.get();
    }

    public Path getPath() {
        return this.path.get();
    }

    public Path resolve(String path) {
        return this.path.get().resolve(path);
    }
}