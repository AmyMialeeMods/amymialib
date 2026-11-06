package dev.amymialee.amymialib.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class BottomlessHashMap<K, V> extends ConcurrentHashMap<K, V> {
    private final Function<K, V> compute;

    public BottomlessHashMap(Function<K, V> compute) {
        this.compute = compute;
    }

    public BottomlessHashMap(int initialCapacity, Function<K, V> compute) {
        super(initialCapacity);
        this.compute = compute;
    }

    public BottomlessHashMap(Map<? extends K, ? extends V> m, Function<K, V> compute) {
        super(m);
        this.compute = compute;
    }

    public BottomlessHashMap(int initialCapacity, float loadFactor, Function<K, V> compute) {
        super(initialCapacity, loadFactor);
        this.compute = compute;
    }

    public BottomlessHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, Function<K, V> compute) {
        super(initialCapacity, loadFactor, concurrencyLevel);
        this.compute = compute;
    }

    public BottomlessHashMap(Supplier<V> compute) {
        this(k -> compute.get());
    }

    public BottomlessHashMap(int initialCapacity, Supplier<V> compute) {
        this(initialCapacity, k -> compute.get());
    }

    public BottomlessHashMap(Map<? extends K, ? extends V> m, Supplier<V> compute) {
        this(m, k -> compute.get());
    }

    public BottomlessHashMap(int initialCapacity, float loadFactor, Supplier<V> compute) {
        this(initialCapacity, loadFactor, k -> compute.get());
    }

    public BottomlessHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, Supplier<V> compute) {
        this(initialCapacity, loadFactor, concurrencyLevel, k -> compute.get());
    }

    public BottomlessHashMap(V compute) {
        this(k -> compute);
    }

    public BottomlessHashMap(int initialCapacity, V compute) {
        this(initialCapacity, k -> compute);
    }

    public BottomlessHashMap(Map<? extends K, ? extends V> m, V compute) {
        this(m, k -> compute);
    }

    public BottomlessHashMap(int initialCapacity, float loadFactor, V compute) {
        this(initialCapacity, loadFactor, k -> compute);
    }

    public BottomlessHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, V compute) {
        this(initialCapacity, loadFactor, concurrencyLevel, k -> compute);
    }

    @Override
    public V get(Object key) {
        return super.computeIfAbsent((K) key, compute);
    }

    @Override @ApiStatus.Internal
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return super.computeIfAbsent(key, mappingFunction);
    }
}