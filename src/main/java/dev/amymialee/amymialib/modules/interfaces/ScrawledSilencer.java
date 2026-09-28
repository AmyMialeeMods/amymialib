package dev.amymialee.amymialib.modules.interfaces;

public interface ScrawledSilencer<T> {
    default boolean amy$isSilenced() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }

    default T amy$silence() { throw new UnsupportedOperationException("Implemented via mixin."); }

    default T amy$setSilenced(boolean silenced) { throw new UnsupportedOperationException("Implemented via mixin."); }
}