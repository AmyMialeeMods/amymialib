package dev.amymialee.amymialib.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class BottomlessList<T> extends ArrayList<T> {
    private final Function<Integer, T> defaulted;

    public BottomlessList(Function<Integer, T> defaulted, int initialCapacity) {
        super(initialCapacity);
        this.defaulted = defaulted;
    }

    public BottomlessList(Function<Integer, T> defaulted) {
        this.defaulted = defaulted;
    }

    public BottomlessList(Function<Integer, T> defaulted, @NotNull Collection<? extends T> c) {
        super(c);
        this.defaulted = defaulted;
    }

    public BottomlessList(Supplier<T> defaulted, int initialCapacity) {
        this(i -> defaulted.get(), initialCapacity);
    }

    public BottomlessList(Supplier<T> defaulted) {
        this(i -> defaulted.get());
    }

    public BottomlessList(Supplier<T> defaulted, @NotNull Collection<? extends T> c) {
        this(i -> defaulted.get(), c);
    }

    public BottomlessList(T defaulted, int initialCapacity) {
        this(i -> defaulted, initialCapacity);
    }

    public BottomlessList(T defaulted) {
        this(i -> defaulted);
    }

    public BottomlessList(T defaulted, @NotNull Collection<? extends T> c) {
        this(i -> defaulted, c);
    }

    @Override
    public T get(int index) {
        while (index >= this.size()) this.add(this.defaulted.apply(this.size()));
        return super.get(index);
    }

    @Override
    public T getFirst() {
        while (0 == this.size()) this.add(this.defaulted.apply(0));
        return super.getFirst();
    }

    @Override
    public T getLast() {
        while (0 == this.size()) this.add(this.defaulted.apply(0));
        return super.getLast();
    }

    @Override
    public T set(int index, T element) {
        while (index >= this.size()) this.add(this.defaulted.apply(this.size()));
        return super.set(index, element);
    }
}