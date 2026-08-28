package dev.amymialee.amymialib.aspects;

import dev.amymialee.amymialib.util.AMDirectoryInternal;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.function.Supplier;

public enum AspectSaveLocation {
    CONFIG(true, () -> FabricLoader.getInstance().getConfigDir().resolve("amymialib").resolve("config")),
    WORLD(true, () -> null),
    USER(false, () -> AMDirectoryInternal.getPath("aspects")),
    CLIENT(false, () -> FabricLoader.getInstance().getConfigDir().resolve("amymialib").resolve("client"));

    public final boolean serverBased;
    public final boolean clientBased;
    public final Supplier<Path> path;

    AspectSaveLocation(boolean serverBased, @NonNull Supplier<Path> path) {
        this.serverBased = serverBased;
        this.clientBased = !serverBased;
        this.path = path;
    }

    public @NonNull Path resolve(String path) {
        return this.path.get().resolve(path);
    }
}
