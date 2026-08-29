package dev.amymialee.amymialib.aspects.builder;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.Aspect;
import dev.amymialee.amymialib.aspects.AspectManager;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;

public interface AspectFileManager {
    static void saveAll() {
        AspectManager.ASPECTS.values().forEach(AspectFileManager::save);
    }

    static <T> void save(@NonNull Aspect<T> aspect) {
        if (!aspect.saveLocation.shouldSave()) return;
        var config = new JsonObject();
        config.add("value", aspect.type.codec.encodeStart(JsonOps.INSTANCE, aspect.get()).getOrThrow());
        try {
            Files.createDirectories(aspect.filePath.getParent());
        } catch (IOException e) {
            AmyMialib.LOGGER.error("Couldn't write Aspect file for {}", aspect.id, e);
        }
        try (var fileWriter = Files.newBufferedWriter(aspect.filePath)) {
            fileWriter.write(AmyMialib.GSON.toJson(config));
        } catch (IOException e) {
            AmyMialib.LOGGER.error("Couldn't write Aspect file for {}", aspect.id, e);
        }
    }

    static void loadAll() {
        AspectManager.ASPECTS.values().forEach(AspectFileManager::load);
    }

    static <T> void load(@NonNull Aspect<T> aspect) {
        JsonObject data = null;
        try {
            data = AmyMialib.GSON.fromJson(Files.readString(aspect.filePath), JsonObject.class);
        } catch (Exception _) {}
        if (data == null) return;
        var value = data.get("value");
        if (value != null) aspect.type.codec.decode(JsonOps.INSTANCE, value).result().ifPresent(v -> aspect.loadFromFile(v.getFirst()));
    }
}
