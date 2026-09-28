package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.dossier.DossierRegistries;
import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ScribbleBannerPattern extends Scribble<Item> implements DossierRegistries, DossierTags {
    public final ScribbleTag<BannerPattern> patternTag;
    public final ScribbleKey<BannerPattern> patternKey;

    public ScribbleBannerPattern(Scribbler scribbler, String path, @Nullable Supplier<Holder.Reference<Item>> getter) {
        super(scribbler, BuiltInRegistries.ITEM, getter);
        patternTag = scribbler.tag(Registries.BANNER_PATTERN, "pattern_item/%s".formatted(path));
        patternKey = scribbler.key(Registries.BANNER_PATTERN, path);
        scribbler.dossierData.add(this);
    }

    @Override
    public ScribbleBannerPattern translation(String name) {
        patternTag.translation("%s Banner Patterns".formatted(name));
        patternKey.dyeTranslation("block.%s.banner.%s.%s".formatted(this.key().identifier().getNamespace(), this.key().identifier().getPath(), "%s"), "%s %s".formatted("%s", name));
        super.translation("%s Banner Pattern".formatted(name));
        return this;
    }

    @Override
    public void commitRegistries(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.BANNER_PATTERN, registerable -> registerable.register(patternKey.get(), new BannerPattern(patternKey.get().identifier(), "block.%s.banner.%s".formatted(patternKey.get().identifier().getNamespace(), patternKey.get().identifier().getPath()))));
    }

    @Override
    public void commitTags(Provider provider) {
        provider.tag(patternTag.get()).addOptional(patternKey.get());
        provider.tag(ItemTags.LOOM_PATTERNS).add(this.key());
    }

    /**
     * Adds a translation to generate with datagen.
     */
    @Override
    public ScribbleBannerPattern translation(String key, String translation) {
        return (ScribbleBannerPattern) super.translation(key, translation);
    }

    /**
     * Creates a translation with every dye colour included as options.
     * @param key Translation key, including %s for dye colour
     * @param translation Translation, including %s for dye colour
     */
    @Override
    public ScribbleBannerPattern dyeTranslation(String key, String translation) {
        return (ScribbleBannerPattern) super.dyeTranslation(key, translation);
    }

    /**
     * Adds this object to given tags.
     */
    @Override
    public ScribbleBannerPattern addTo(TagKey... targets) {
        return (ScribbleBannerPattern) super.addTo(targets);
    }

    /**
     * Adds this object to given tags.
     */
    @Override
    public ScribbleBannerPattern addTo(List<TagKey> targets1, TagKey ... targets2) {
        return (ScribbleBannerPattern) super.addTo(targets1, targets2);
    }

    @Override
    public String name() {
        return "Banner Pattern";
    }
}