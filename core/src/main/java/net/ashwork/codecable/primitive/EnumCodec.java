/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.primitive;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.function.DataResultFunction;
import net.ashwork.codecable.function.ThrowingFunction;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A codec for an enum. Encodes and decodes the enum to a representable string.
 * When compressed, encodes and decodes the enum to a representable {@code int}.
 *
 * @param <E> the type of the enum
 */
public final class EnumCodec<E extends Enum<E>> implements Codecable<E>, PrimitiveCodec<E> {

    private final Function<E, String> toString;
    private final ToIntFunction<E> toInt;
    private final ThrowingFunction<String, E> fromString;
    private final ThrowingFunction<Integer, E> fromInt;

    /**
     * Default constructor.
     *
     * @param fromString a function that transforms a string into an enum or throws
     *                   an exception
     * @param toString a function that transforms an enum into a string or throws
     *                 an exception
     * @param fromInt a function that transforms an {@code int} into an enum or
     *                throws an exception
     * @param toInt a function that transforms an enum into an {@code int} or throws
     *              an exception
     */
    public EnumCodec(final ThrowingFunction<String, E> fromString, final Function<E, String> toString, final ThrowingFunction<Integer, E> fromInt, final ToIntFunction<E> toInt) {
        this.fromString = fromString;
        this.toString = toString;
        this.fromInt = fromInt;
        this.toInt = toInt;
    }

    @Override
    public <T> DataResult<E> read(final DynamicOps<T> ops, final T input) {
        return ops.compressMaps() ?
                ops.getNumberValue(input)
                        .map(Number::intValue)
                        .flatMap(new DataResultFunction<>(fromInt, (i, e) -> "Unknown enum id: " + i))
                : ops.getStringValue(input).flatMap(new DataResultFunction<>(fromString, (s, e) -> "Unknown enum string: " + s));
    }

    @Override
    public <T> T write(final DynamicOps<T> ops, final E value) {
        return ops.compressMaps() ? ops.createInt(toInt.applyAsInt(value))
                : ops.createString(toString.apply(value));
    }

    @Override
    public String toString() {
        return "Enum";
    }
}
