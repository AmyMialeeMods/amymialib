package dev.amymialee.amymialib.util.registry;

import com.ibm.icu.impl.Pair;
import dev.amymialee.amymialib.util.AMRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public class AMRegistryInstance<T> {
   public final AMRegistry amRegistry;
   public final Registry<T> registry;
   public final ResourceKey<T> key;
   public final T value;
   public Pair<String, String> translation;

    public AMRegistryInstance(AMRegistry amRegistry, Registry<T> registry, ResourceKey<T> key, T value) {
        this.amRegistry = amRegistry;
        this.registry = registry;
        this.key = key;
        this.value = value;
    }

    public AMRegistryInstance<T> translation(String translation) {
        this.translation = Pair.of(Util.makeDescriptionId(registry.key().identifier().toLanguageKey(), key.identifier()), translation);
        return this;
    }

    public AMRegistryInstance<T> translation(String key, String translation) {
        this.translation = Pair.of(key, translation);
        return this;
    }

    public AMRegistryInstance<T> translation(Pair<String, String> translation) {
        this.translation = translation;
        return this;
    }

    public void preRegister() {}

    public void addTranslation() {
        if (this.translation != null) this.amRegistry.addTranslation(this.translation);
    }

    public void postRegister() {}

    public T register() {
        this.preRegister();
        var value = Registry.register(registry, key, this.value);
        this.addTranslation();
        this.postRegister();
        return value;
    }

    public Holder.@NonNull Reference<T> registerHolder() {
        this.preRegister();
        var value = Registry.registerForHolder(registry, key, this.value);
        this.addTranslation();
        this.postRegister();
        return value;
    }
}