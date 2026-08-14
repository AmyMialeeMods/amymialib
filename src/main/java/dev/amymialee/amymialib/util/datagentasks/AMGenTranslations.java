package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenTranslations extends AMNameProvider {
    default void generateTranslations(AMLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMLanguageProvider(new AMGenTranslations() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateTranslations(AMLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {
                for (var object : list) if (object instanceof AMGenTranslations task) task.generateTranslations(provider, registryLookup, builder);
            }
        }, dataOutput, future);
    }

    class AMLanguageProvider extends FabricLanguageProvider {
        private final AMGenTranslations dataGen;

        public AMLanguageProvider(AMGenTranslations gen, FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, registryLookup);
            this.dataGen = gen;
        }

        @Override
        public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
            this.dataGen.generateTranslations(this, registryLookup, translationBuilder);
        }

        public void addSubtitleKey(@NonNull TranslationBuilder translationBuilder, @NotNull SoundEvent event, String translation) {
            var soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(event.location());
            if (soundEvent == null || soundEvent.getSubtitle() == null) return;
            if (!(soundEvent.getSubtitle().getContents() instanceof TranslatableContents translatable)) return;
            translationBuilder.add(translatable.getKey(), translation);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}