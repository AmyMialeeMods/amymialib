package dev.amymialee.amymialib.modules.scribbler.helpers;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;

import java.util.List;

public record MaterialHelper(TagKey<Item> materials, ToolMaterial tool, ArmorMaterial armor) implements AmyMialibTaggable {
    @Override
    public List<TagKey> amy$tags() {
        return List.of(materials);
    }
}