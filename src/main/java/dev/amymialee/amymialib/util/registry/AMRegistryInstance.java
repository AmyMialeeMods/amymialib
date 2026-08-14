package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import dev.amymialee.amymialib.util.datagentasks.AMGenTranslations;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public class AMRegistryInstance<T> implements AMGenTranslations {
    public final AMRegistry amRegistry;
    public final Registry<T> registry;
    public final ResourceKey<T> key;
    public final T value;

    public AMRegistryInstance(AMRegistry amRegistry, Registry<T> registry, ResourceKey<T> key, T value) {
        this.amRegistry = amRegistry;
        this.registry = registry;
        this.key = key;
        this.value = value;
    }

    public AMRegistryInstance<T> translation(String translation) {
        this.amRegistry.datagenTasks.add(new AMGenTranslations() {
            @Override
            public void generateTranslations(AMLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {
                builder.add(Util.makeDescriptionId(registry.key().identifier().getPath(), key.identifier()), translation);
            }
        });
        return this;
    }

    public AMRegistryInstance<T> translation(String key, String translation) {
        this.amRegistry.datagenTasks.add(new AMGenTranslations() {
            @Override
            public void generateTranslations(AMLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {
                builder.add(key, translation);
            }
        });
        return this;
    }

    public void preRegister() {}

    public void informRegistry() {
        this.amRegistry.registered.add(this);
        this.amRegistry.putKey(this.value, this.key);
    }

    public void postRegister() {}

    public T register() {
        this.preRegister();
        var value = Registry.register(registry, key, this.value);
        this.informRegistry();
        this.postRegister();
        return value;
    }

    public ResourceKey<T> registerKey() {
        this.preRegister();
        Registry.register(registry, key, this.value);
        this.informRegistry();
        this.postRegister();
        return key;
    }

    public Holder.@NonNull Reference<T> registerHolder() {
        this.preRegister();
        var value = Registry.registerForHolder(registry, key, this.value);
        this.informRegistry();
        this.postRegister();
        return value;
    }
}