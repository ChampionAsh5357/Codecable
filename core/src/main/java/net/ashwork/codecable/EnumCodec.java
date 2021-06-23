/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.ashwork.codecable.util.ErrorUtil;
import net.ashwork.functionality.callable.CallableFunction;
import net.ashwork.functionality.callable.CallableIntFunction;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A codec for an enum that will handle it as a string when
 * uncompressed and an integer when compressed.
 *
 * @param <E> The enum type
 */
public final class EnumCodec<E extends Enum<E>> implements Codecable<E> {

    private final Class<E> enumClass;
    private final CallableFunction<? super String, ? extends E> fromString;
    private final Function<? super E, ? extends String> toString;
    private final ToIntFunction<E> toInt;
    private final CallableIntFunction<E> fromInt;

    public EnumCodec(final Class<E> enumClass) {
        this(enumClass, str -> Enum.valueOf(enumClass, str), Enum::name);
    }

    public EnumCodec(final Class<E> enumClass, final CallableFunction<? super String, ? extends E> fromString, final Function<? super E, ? extends String> toString) {
        this(enumClass, fromString, toString, ordinal -> enumClass.getEnumConstants()[ordinal], Enum::ordinal);
    }

    public EnumCodec(final Class<E> enumClass, final CallableFunction<? super String, ? extends E> fromString, final Function<? super E, ? extends String> toString, final CallableIntFunction<E> fromInt, final ToIntFunction<E> toInt) {
        this.enumClass = enumClass;
        if (this.enumClass.getEnumConstants().length == 0) {
            throw new IllegalArgumentException("Cannot implement an enum with 0 entries: " + this.enumClass.getSimpleName());
        }
        this.fromString = fromString;
        this.toString = toString;
        this.fromInt = fromInt;
        this.toInt = toInt;
    }

    @Override
    public <T> DataResult<T> encode(final E input, final DynamicOps<T> ops, final T prefix) {
        return ops.mergeToPrimitive(prefix, ops.compressMaps()
                ? ops.createInt(this.toInt.applyAsInt(input))
                : ops.createString(this.toString.apply(input)));
    }

    @Override
    public <T> DataResult<Pair<E, T>> decode(final DynamicOps<T> ops, final T input) {
        return (ops.compressMaps() ? ops.getNumberValue(input).flatMap(ordinal ->
                Optional.ofNullable(ErrorUtil.nullCall(() -> this.fromInt.apply(ordinal.intValue())))
                        .map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element id: " + ordinal)))
                : ops.getStringValue(input).flatMap(str ->
                Optional.ofNullable(ErrorUtil.nullCall(() -> this.fromString.apply(str)))
                        .map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element name: " + str)))
        ).map(e -> Pair.of(e, ops.empty()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumClass);
    }

    @Override
    public String toString() {
        return "EnumCodec[" + this.enumClass.getSimpleName() + ']';
    }
}
