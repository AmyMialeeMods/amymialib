package dev.amymialee.amymialib.aspects;

import dev.amymialee.amymialib.util.AMEnvironmentType;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.function.Supplier;

public enum AspectSaveLocation {
    CONFIG(() -> AMEnvironmentType.getEnv().isServer, () -> FabricLoader.getInstance().getConfigDir().resolve("amymialib").resolve("config")),
    CLIENT(() -> AMEnvironmentType.getEnv().isClient, () -> FabricLoader.getInstance().getConfigDir().resolve("amymialib").resolve("client"));

    private final Supplier<Boolean> shouldSave;
    private final Supplier<Path> path;

    AspectSaveLocation(@NonNull Supplier<Boolean> shouldSave, @NonNull Supplier<Path> path) {
        this.shouldSave = shouldSave;
        this.path = path;
    }

    public boolean shouldSave() {
        return this.shouldSave.get();
    }

    public Path getPath() {
        return this.path.get();
    }

    public @NonNull Path resolve(String path) {
        return this.path.get().resolve(path);
    }
}