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
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.ashwork.functionality.callable.CallableFunction;
import net.ashwork.functionality.callable.CallableIntFunction;

import java.util.Set;
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
}
