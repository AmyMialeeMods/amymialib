package dev.amymialee.amymialib.modules;

import dev.amymialee.amymialib.AmyMialib;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface AMDirectoryInternal {
    @Nullable String AMYMIALIB_DIRECTORY_OVERRIDE = System.getProperty("amymialib.directory");

    static Path getPath(Path relative) {
        return MDirImpl.INSTANCE.path.resolve(relative);
    }

    static Path getPath(String fileName) {
        return MDirImpl.INSTANCE.path.resolve(fileName);
    }

    static Path getPath(String first, String... more) {
        return MDirImpl.INSTANCE.path.resolve(Path.of(first, more));
    }

    static File getFile(String fileName) {
        return getPath(fileName).toFile();
    }

    class MDirImpl {
        private static final MDirImpl INSTANCE = new MDirImpl();
        private final Path path;

        public MDirImpl() {
            if (AMYMIALIB_DIRECTORY_OVERRIDE != null) {
                AmyMialib.LOGGER.info("amymialib directory overridden, setting to {}", AMYMIALIB_DIRECTORY_OVERRIDE);
                this.path = Paths.get(AMYMIALIB_DIRECTORY_OVERRIDE);
                return;
            }
            var userHome = System.getProperty("user.home");
            var fallback = Path.of(userHome, ".amymialib");
            this.path = switch (Util.getPlatform()) {
                case WINDOWS -> {
                    var appData = System.getenv("AppData");
                    if (appData == null) {
                        AmyMialib.LOGGER.warn("No value for AppData env var even though we're on windows, falling back to {}", fallback);
                        yield fallback;
                    }
                    yield Path.of(appData, ".amymialib");
                }
                case OSX -> Path.of(userHome, "Library", "Application Support", "amymialib");
                case LINUX -> {
                    var xdgData = System.getenv("XDG_DATA_HOME");
                    var localShare = Path.of(userHome, ".local", "share", "amymialib");
                    if (xdgData == null) {
                        AmyMialib.LOGGER.warn("No value for XDG_DATA_HOME, falling back to {}", localShare);
                        yield localShare;
                    }
                    yield Path.of(xdgData, "amymialib");
                }
                case UNKNOWN, SOLARIS -> fallback;
            };
            AmyMialib.LOGGER.info("Set AmyMialib directory to {}", this.path);
        }
    }
}