package dev.amymialee.amymialib.modules.iinterfaces;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Defines something that can be added to a tag.<p>
 * This includes:<p>
 * - Tags<p>
 * - ResourceKeys<p>
 * - BlockItemTagIds<p>
 * - BlockItemIds<p>
 * - Anything that can be converted to a ResourceKey without much difficulty:<p>
 *     <pre>
 *     <p>- BlockEntityType</p>
 *     <p>- Block</p>
 *     <p>- EntityType</p>
 *     <p>- Fluid</p>
 *     <p>- Item</p>
 *     </pre>
 */
public interface AmyMialibTaggable {
    default List<ResourceKey> amy$keys() {
        return List.of();
    }

    default List<TagKey> amy$tags() {
        return List.of();
    }
}