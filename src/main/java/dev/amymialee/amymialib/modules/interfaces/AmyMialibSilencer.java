package dev.amymialee.amymialib.modules.interfaces;

public interface AmyMialibSilencer<T> {
    default boolean amymialib$isSilenced() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }

    default T amymialib$silence() { throw new UnsupportedOperationException("Implemented via mixin."); }

    default T amymialib$setSilenced(boolean silenced) { throw new UnsupportedOperationException("Implemented via mixin."); }
}