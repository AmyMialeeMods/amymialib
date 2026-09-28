package dev.amymialee.amymialib.modules.scribbler;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.text.WordUtils;

public record BannerHelper(Scribbler scribbler, Item item, TagKey<BannerPattern> patternTag, ResourceKey<BannerPattern> patternKey) {
    public BannerHelper amy$translate(String translation) {
        this.scribbler.translation(Util.makeDescriptionId("tag.%s".formatted(patternTag.registry().identifier().getPath()), patternTag.location()), "%s Banner Patterns".formatted(translation));
        for (var dye : DyeColor.values()) scribbler.translation("block.%s.banner.%s.%s".formatted(patternKey.identifier().getNamespace(), patternKey.identifier().getPath(), dye.getName()), "%s %s".formatted(WordUtils.capitalize(dye.getName().replace('_', ' ')), translation));
        item.amy$translate("%s Banner Pattern".formatted(translation));
        return this;
    }
}