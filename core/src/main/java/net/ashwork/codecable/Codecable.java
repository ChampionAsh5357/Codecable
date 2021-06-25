/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.ashwork.functionality.callable.CallableFunction;
import net.ashwork.functionality.callable.CallableIntFunction;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * The basic interface for all general codecs
 * within Codecable.
 *
 * @param <A> The element type
 */
public interface Codecable<A> extends Codec<A> {

    /**
     * Wraps a codec to the following codecable interface.
     *
     * @param codec A codec
     * @param <E> The object type
     * @return The wrapped codec as {@link Codecable}
     */
    static <E> Codecable<E> wrap(final Codec<E> codec) {
        return new CodecableWrapper<>(codec);
    }

    /**
     * Constructs an iterator codec from the supplied element codec.
     *
     * @param elementCodec The codec for the elements
     * @param <E> The type of the elements in the iterator
     * @return An iterator codec
     */
    static <E> Codecable<Iterator<E>> iterator(final Codec<E> elementCodec) {
        return wrap(elementCodec.listOf().xmap(List::iterator, ImmutableList::copyOf));
    }

    /**
     * @return Creates an iterator of the given codec
     * @see Codecable#iterator(Codec)
     */
    default Codecable<Iterator<A>> iteratorOf() {
        return iterator(this);
    }

    /**
     * Constructs an enumeration codec from the supplied element codec.
     *
     * @param elementCodec The codec for the elements
     * @param <E> The type of the elements in the iterator
     * @return An enumeration codec
     */
    static <E> Codecable<Enumeration<E>> enumeration(final Codec<E> elementCodec) {
        return wrap(iterator(elementCodec).xmap(Iterators::asEnumeration, Enumeration::asIterator));
    }

    /**
     * @return Creates an enumeration of the given codec
     * @see Codecable#enumeration(Codec)
     */
    default Codecable<Enumeration<A>> enumerationOf() {
        return enumeration(this);
    }

    /**
     * Constructs a set codec that ignores duplicate elements.
     *
     * @param elementCodec The codec of the elements within the set
     * @param <E> The element type
     * @return A set codec
     */
    static <E> Codecable<Set<E>> set(final Codec<E> elementCodec) {
        return wrap(elementCodec.listOf().xmap(ImmutableSet::copyOf, ImmutableList::copyOf));
    }

    /**
     * @return Creates a set of the given codec
     * @see Codecable#set(Codec)
     */
    default Codecable<Set<A>> setOf() { return set(this); }

    /**
     * Constructs a set codec that fails on duplicate elements.
     *
     * @param elementCodec The codec of the elements within the set
     * @param <E> The element type
     * @return A {@link SetCodec}
     */
    static <E> Codecable<Set<E>> strictSet(final Codec<E> elementCodec) {
        return new SetCodec<>(elementCodec);
    }

    /**
     * @return Creates a strict set of the given codec
     * @see Codecable#strictSet(Codec)
     */
    default Codecable<Set<A>> strictSetOf() { return strictSet(this); }

    /**
     * Constructs an enum codec that, when compressed, will write
     * using the associated ordinals. When uncompressed, it will
     * read and write the name associated with the enum value itself.
     *
     * @param enumClass The enum class
     * @param <E> The enum type
     * @return An {@link EnumCodec}
     */
    static <E extends Enum<E>> Codecable<E> enumCodec(final Class<E> enumClass) {
        return new EnumCodec<>(enumClass);
    }

    /**
     * Constructs an enum codec that, when compressed, will write
     * using the associated ordinals.
     *
     * @param enumClass The enum class
     * @param fromString A function to get an enum from a string
     * @param toString A function to get a string representing the enum
     * @param <E> The enum type
     * @return An {@link EnumCodec}
     */
    static <E extends Enum<E>> Codecable<E> enumCodec(final Class<E> enumClass, final CallableFunction<? super String, ? extends E> fromString, final Function<? super E, ? extends String> toString) {
        return new EnumCodec<>(enumClass, fromString, toString);
    }

    /**
     * Constructs an enum codec.
     *
     * @param enumClass The enum class
     * @param fromString A function to get an enum from a string
     * @param toString A function to get a string representing an enum
     * @param fromInt A function to get an enum from an integer when compressed
     * @param toInt A function to get an integer representing an enum when compressed
     * @param <E> The enum type
     * @return An {@link EnumCodec}
     */
    static <E extends Enum<E>> Codecable<E> enumCodec(final Class<E> enumClass, final CallableFunction<? super String, ? extends E> fromString, final Function<? super E, ? extends String> toString, final CallableIntFunction<E> fromInt, final ToIntFunction<E> toInt) {
        return new EnumCodec<>(enumClass, fromString, toString, fromInt, toInt);
    }

    /**
     * A codec that encodes a map into a list of pairs containing a list of keys related to a particular value.
     *
     * @param keyListCodec The codec that represents the list of keys related to a particular value in the map
     * @param valueCodec The codec that represents the value within the map
     * @param <K> The type of the keys maintained by this map
     * @param <V> The type of the values in the map
     * @return A codec which encodes a map into pairs of a list of keys and a value
     */
    static <K, V> Codecable<Map<K, V>> keyListMapCodec(final MapCodec<List<K>> keyListCodec, final MapCodec<V> valueCodec) {
        return wrap(Codec.mapPair(keyListCodec, valueCodec).codec().listOf().comapFlatMap(list -> {
            Map<K, V> success = new HashMap<>(), duplicates = new HashMap<>();
            list.forEach(pair -> pair.getFirst().forEach(key -> {
                if (success.putIfAbsent(key, pair.getSecond()) != null)
                    duplicates.putIfAbsent(key, pair.getSecond());
            }));
            return createMapResult(success, duplicates);
        }, map -> {
            Map<V, ImmutableList.Builder<K>> inverse = new HashMap<>();
            map.forEach((key, value) -> inverse.computeIfAbsent(value, v -> ImmutableList.builder()).add(key));
            return inverse.entrySet().stream().map(entry -> Pair.of((List<K>) entry.getValue().build(), entry.getKey())).toList();
        }));
    }

    /**
     * A codec that encodes a map into a list of pairs containing a key and its value.
     *
     * @param keyCodec The codec that represents the keys maintained by the map
     * @param valueCodec The codec that represents the value within the map
     * @param <K> The type of the keys maintained by this map
     * @param <V> The type of the values in the map
     * @return A codec which encodes a map into pairs of a key and a value
     */
    static <K, V> Codecable<Map<K, V>> listMapCodec(final MapCodec<K> keyCodec, final MapCodec<V> valueCodec) {
        return wrap(Codec.mapPair(keyCodec, valueCodec).codec().listOf().comapFlatMap(list -> {
            Map<K, V> success = new HashMap<>(), duplicates = new HashMap<>();
            list.forEach(pair -> {
                if (success.putIfAbsent(pair.getFirst(), pair.getSecond()) != null)
                    duplicates.putIfAbsent(pair.getFirst(), pair.getSecond());
            });
            return createMapResult(success, duplicates);
        }, map -> map.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList()));
    }

    /**
     * Creates a data result based on the successful and failed entries within the map.
     * If there are any duplicates entries, the result will error, otherwise, it will succeed.
     *
     * @param success A map of all the entries that are read
     * @param duplicates A map of all the entries that are duplicates
     * @param <K> The type of the keys maintained by this map
     * @param <V> The type of the values in the map
     * @return A {@link DataResult} that holds the result of the map and an error message if any
     *         duplicates were present.
     */
    private static <K, V> DataResult<Map<K, V>> createMapResult(final Map<K, V> success, final Map<K, V> duplicates) {
        ImmutableMap<K, V> result = ImmutableMap.copyOf(success);
        return duplicates.isEmpty()
                ? DataResult.success(result)
                : DataResult.error("Duplicate keys were found in the map: ["
                + duplicates.entrySet().stream().reduce("",
                (prefix, entry) -> (prefix.isEmpty() ? "" : prefix + ", ") + entry.getKey() + " -> " + entry.getValue(),
                (first, second) -> (first.isEmpty() ? "" : first + ", ") + second) + "]", result);
    }
}
