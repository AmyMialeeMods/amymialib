package dev.amymialee.amymialib.util;

import java.util.Optional;
import java.util.function.Supplier;

public interface OptionalUtils {
    static Optional<Integer> tryParseInt(String string) {
        return tryParseInt(() -> string);
    }

    static Optional<Integer> tryParseInt(Supplier<String> string) {
        try {
            return Optional.of(Integer.parseInt(string.get()));
        } catch (Throwable ignored) {}
        return Optional.empty();
    }

    static Optional<Float> tryParseFloat(String string) {
        return tryParseFloat(() -> string);
    }

    static Optional<Float> tryParseFloat(Supplier<String> string) {
        try {
            return Optional.of(Float.parseFloat(string.get()));
        } catch (Throwable ignored) {}
        return Optional.empty();
    }

    static Optional<Double> tryParseDouble(String string) {
        return tryParseDouble(() -> string);
    }

    static Optional<Double> tryParseDouble(Supplier<String> string) {
        try {
            return Optional.of(Double.parseDouble(string.get()));
        } catch (Throwable ignored) {}
        return Optional.empty();
    }
}