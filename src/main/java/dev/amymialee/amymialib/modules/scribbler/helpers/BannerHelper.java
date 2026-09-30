package dev.amymialee.amymialib.modules.scribbler.helpers;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;

public record BannerHelper(Item item, TagKey<BannerPattern> tag, ResourceKey<BannerPattern> key) {}