package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.Decipher;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import dev.amymialee.amymialib.modules.scribbler.helpers.BannerHelper;
import dev.amymialee.amymialib.modules.scribbler.helpers.BlockBatch;
import dev.amymialee.amymialib.modules.scribbler.helpers.DiscHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SoundType;
import org.apache.commons.text.WordUtils;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.AccessFlag;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierTranslations extends DossierLanyard {
    Event<TryTranslate> TRY_TRANSLATE = EventFactory.createArrayBacked(TryTranslate.class, (listeners) -> (lookup, builder, object, args) -> {
        for (var event : listeners) if (event.translate(lookup, builder, object, args)) return true;
        return false;
    });
    Event<TryTranslateKey> TRY_TRANSLATE_KEY = EventFactory.createArrayBacked(TryTranslateKey.class, (listeners) -> (lookup, builder, object, args) -> {
        for (var event : listeners) event.translate(lookup, builder, object, args);
    });

    void commitTranslations(Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder);

    @FunctionalInterface
    interface Ctx extends DossierTranslations {
        @Override
        default void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder) {
            commitTranslations(provider);
        }

        void commitTranslations(Provider ctx);
    }

    @FunctionalInterface
    interface Builder extends DossierTranslations {
        @Override
        default void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder) {
            commitTranslations(builder);
        }

        void commitTranslations(FabricLanguageProvider.TranslationBuilder builder);
    }

    @Override default String name() { return "Translations"; }

    final class Provider extends FabricLanguageProvider {
        private final Dossier dossier;
        private HolderLookup.Provider lookup;
        private TranslationBuilder builder;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
        }

        @Override
        public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
            AutoQueue.forEach(dossier.scribbler, this::runTranslate);
            this.lookup = lookup;
            this.builder = builder;
            this.dossier.registryEach(p -> p.commitTranslations(this, lookup, builder), DossierTranslations.class);
        }

        private void runTranslate(String packageName) {
            AutoScanner.findAnnotations(packageName).getFieldsAnnotatedWith(Decipher.class).forEach(f -> {
                try {
                    var field = f.get(null);
                    for (var decipher : f.getAnnotationsByType(Decipher.class)) {
                        if (field instanceof Holder.Reference<?> holder) {
                            dossier.scribbler.dossierData.add((DossierTranslations) (_, lookup, builder) -> TRY_TRANSLATE.invoker().translate(lookup, builder, holder.value(), decipher.value()));
                        } else if (field instanceof AmyMialibTaggable taggable) {
                            for (var tag : taggable.amymialib$tags()) dossier.scribbler.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(tag.getTranslationKey(), decipher.value()[0]));
                            for (var key : taggable.amymialib$keys()) dossier.scribbler.dossierData.add((DossierTranslations) (_, lookup, builder) -> {
                                builder.add(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), decipher.value()[0]);
                                TRY_TRANSLATE_KEY.invoker().translate(lookup, builder, key, decipher.value());
                            });
                        } else {
                            dossier.scribbler.dossierData.add((DossierTranslations) (_, lookup, builder) -> TRY_TRANSLATE.invoker().translate(lookup, builder, field, decipher.value()));
                        }
                    }
                } catch (Throwable t) {
                    var reason = "";
                    if (!f.accessFlags().contains(AccessFlag.STATIC)) {
                        reason = "Unable to translate field %s.%s, as it needs to be static.".formatted(f.getDeclaringClass().getName(), f.getName());
                    } else if (t instanceof RuntimeException runtimeException) {
                        throw runtimeException;
                    } else {
                        dossier.scribbler.logger.error("Unable to translate field {}.{}, due to an unknown reason.", f.getDeclaringClass().getName(), f.getName());
                        throw new RuntimeException(t);
                    }
                    dossier.scribbler.logger.error(reason);
                    throw new RuntimeException(reason, null, true, false) {};
                }
            });
        }

        public HolderLookup.Provider lookup() {
            return lookup;
        }

        public TranslationBuilder builder() {
            return builder;
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }

    @ScribbleInit
    static void init() {
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof String s) {
                builder.add(s, args[0]);
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof Scribbler s) {
                builder.add(s.id(s.namespace).toLanguageKey(), args[0]);
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof EntityType<?> entityType) {
                builder.add(entityType, args[0]);
                Optional.ofNullable(entityType.amy$asItem()).ifPresent(e -> builder.add(e, "%s %s".formatted(args[0], "Spawn Egg")));
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof DiscHelper(var item, var key, var song, var lengthInSeconds, var comparatorOutput)) {
                builder.add(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), args[0]);
                var itemKey = item.builtInRegistryHolder().key();
                builder.add(Util.makeDescriptionId(itemKey.registry().toShortLanguageKey(), itemKey.identifier()), "Music Disc");
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof BannerHelper(var item, var tag, var key)) {
                builder.add(Util.makeDescriptionId("tag.%s".formatted(tag.registry().identifier().getPath()), tag.location()), "%s Banner Patterns".formatted(args[0]));
                for (var dye : DyeColor.values()) builder.add("block.%s.banner.%s.%s".formatted(key.identifier().getNamespace(), key.identifier().getPath(), dye.getName()), "%s %s".formatted(WordUtils.capitalize(dye.getName().replace('_', ' ')), args[0]));
                var itemKey = item.builtInRegistryHolder().key();
                builder.add(Util.makeDescriptionId(itemKey.registry().toShortLanguageKey(), itemKey.identifier()), "%s Banner Pattern".formatted(args[0]));
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof BlockBatch batch) {
                batch.forEach((v, b) -> {
                    var key = batch.get(v).builtInRegistryHolder().key();
                    builder.add(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), v == BlockBatch.Variant.BLOCK ? args[0] : "%s %s".formatted(args[0], WordUtils.capitalizeFully(v.name.replace('_', ' '))));
                });
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof SoundEvent sound) {
                builder.add(Util.makeDescriptionId("subtitles", sound.location()), args[0]);
                return true;
            }
            return false;
        });
        TRY_TRANSLATE.register((lookup, builder, object, args) -> {
            if (object instanceof SoundType type) {
                TRY_TRANSLATE.invoker().translate(lookup, builder, type.getBreakSound(), new String[]{args[0]});
                TRY_TRANSLATE.invoker().translate(lookup, builder, type.getStepSound(), new String[]{args[1]});
                TRY_TRANSLATE.invoker().translate(lookup, builder, type.getPlaceSound(), new String[]{args[2]});
                TRY_TRANSLATE.invoker().translate(lookup, builder, type.getHitSound(), new String[]{args[3]});
                TRY_TRANSLATE.invoker().translate(lookup, builder, type.getFallSound(), new String[]{args[4]});
                return true;
            }
            return false;
        });
        TRY_TRANSLATE_KEY.register((lookup, builder, key, args) -> {
            if (!key.registryKey().equals(Registries.DAMAGE_TYPE)) return;
            var deathMsg = "death.attack." + key.identifier().toShortLanguageKey();
            builder.add(deathMsg, args[1]);
            builder.add(deathMsg + ".player", args[2]); // victim, killer
            builder.add(deathMsg + ".item", args[3]); // victim, killer, item
        });
    }

    @FunctionalInterface
    interface TryTranslate {
        boolean translate(HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder, Object object, String[] args);
    }

    @FunctionalInterface
    interface TryTranslateKey {
        void translate(HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder, ResourceKey<?> object, String[] args);
    }
}