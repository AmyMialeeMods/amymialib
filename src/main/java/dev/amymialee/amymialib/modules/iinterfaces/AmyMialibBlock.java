package dev.amymialee.amymialib.modules.iinterfaces;

import net.minecraft.references.BlockItemId;

public interface AmyMialibBlock {
    default BlockItemId amy$blockItemId() { throw new UnsupportedOperationException("Implemented via mixin."); }
}