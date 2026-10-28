package dev.amymialee.amymialib.modules.aspects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.amymialee.amymialib.AmyMialib;

import java.io.IOException;
import java.nio.file.Files;

public interface AspectFileManager {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static void saveAll() {
        AspectManager.ASPECTS.values().forEach(AspectFileManager::save);
    }

    static <T> void save(Aspect<T> aspect) {
        if (!aspect.saveLocation.shouldSave()) return;
        var config = new JsonObject();
        config.add("value", aspect.type.codec.encodeStart(JsonOps.INSTANCE, aspect.get()).getOrThrow());
        var path = aspect.filePath.get();
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            AmyMialib.LOGGER.error("Couldn't write Aspect file for {}", aspect.id, e);
        }
        try (var fileWriter = Files.newBufferedWriter(path)) {
            fileWriter.write(GSON.toJson(config));
        } catch (IOException e) {
            AmyMialib.LOGGER.error("Couldn't write Aspect file for {}", aspect.id, e);
        }
    }

    static void loadAll() {
        AspectManager.ASPECTS.values().forEach(AspectFileManager::load);
    }

    static <T> void load(Aspect<T> aspect) {
        JsonObject data = null;
        try {
            data = GSON.fromJson(Files.readString(aspect.filePath.get()), JsonObject.class);
        } catch (Exception _) {}
        if (data == null) {
            aspect.loadFromFile(aspect.defaultValue);
            return;
        }
        var value = data.get("value");
        if (value != null) aspect.type.codec.decode(JsonOps.INSTANCE, value).result().ifPresent(v -> aspect.loadFromFile(v.getFirst()));
    }
}
