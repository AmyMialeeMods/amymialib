package dev.amymialee.amymialib.modules.interfaces;

import com.mojang.brigadier.Message;

public interface AmyMialibSimpleCommandExceptionType {
    default Message amymialib$getMessage() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }
}
