/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.util;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utility for creating, modifying, and reading maps.
 */
public final class MapHelper {

    /**
     * Default constructor. Do not initialize.
     */
    private MapHelper() {
        throw new AssertionError("MapHelper should not be initialized");
    }

    /**
     * Creates an immutable map from the given immutable map builder consumer.
     *
     * @param consumer a consumer which operates on the immutable map builder
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a new immutable map
     */
    public static <K, V> Map<K, V> createAndAdd(Consumer<ImmutableMap.Builder<K, V>> consumer) {
        final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        consumer.accept(builder);
        return builder.build();
    }

    /**
     * Creates and adds the given key/value pair to an immutable map.
     *
     * @apiNote
     * Values are stored as a {@link BiConsumer} reading the key and the map builder.
     *
     * @param map a map of key/value builders
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a new immutable map
     */
    public static <K, V> Map<K, V> createAndAdd(final Map<K, BiConsumer<K, ImmutableMap.Builder<K, V>>> map) {
        return createAndAdd(builder -> map.forEach((k, c) -> c.accept(k, builder)));
    }

    /**
     * Adds the collection as a value of an immutable map builder if the collection
     * is not empty.
     *
     * @param map an immutable map builder
     * @param key the key of the entry
     * @param collection the collection to add to the map
     * @param resultFunction a function to transform the collection into the map
     *                       value type
     * @param <K> the type of the map key
     * @param <V0> the type of the elements in the collection
     * @param <V> the type of the map value
     */
    public static <K, V0, V> void addIfNotEmpty(final ImmutableMap.Builder<K, V> map, final K key, final Collection<V0> collection, final Function<Collection<V0>, V> resultFunction) {
        if (!collection.isEmpty())
            map.put(key, resultFunction.apply(collection));
    }

    /**
     * Returns a string representation of a map.
     *
     * <p>The string is returned in a list-like format:
     * <pre>{@code
     *     String output = " key1: value1, key2: value2, ..."
     * }</pre>
     *
     * @param map the map to stringify
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a stringified map
     */
    public static <K, V> String toString(final Map<K, V> map) {
        return map.entrySet().stream().reduce(" ", (str, entry) -> str + ", " + entry.getKey() + ": " + entry.getValue(), (s1, s2) -> s1 + ", " + s2);
    }
}
