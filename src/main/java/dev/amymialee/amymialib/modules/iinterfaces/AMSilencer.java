package dev.amymialee.amymialib.modules.iinterfaces;

public interface AMSilencer<T> {
    default boolean am$isSilenced() {
        return false;
    }

    default T am$silence() { return null; }

    default T am$setSilenced(boolean silenced) { return null; }
}