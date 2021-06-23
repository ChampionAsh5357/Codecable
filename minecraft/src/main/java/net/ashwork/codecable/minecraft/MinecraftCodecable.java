/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft;

import com.mojang.serialization.Codec;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.EnumCodec;
import net.ashwork.codecable.minecraft.util.MinecraftUtil;
import net.ashwork.functionality.callable.CallableFunction;
import net.minecraft.util.StringRepresentable;

/**
 * The basic interface for all general codecs
 * within Codecable in Minecraft.
 *
 * @param <A> The element type
 */
public interface MinecraftCodecable<A> extends Codecable<A> {

    /**
     * Wraps a codec to the following minecraft codecable interface.
     *
     * @param codec A codec
     * @param <E> The object type
     * @return The wrapped codec as {@link MinecraftCodecable}
     */
    static <E> MinecraftCodecable<E> wrap(final Codec<E> codec) {
        return new MinecraftCodecableWrapper<>(codec);
    }

    /**
     * Constructs an enum codec that, when compressed, will write
     * using the associated ordinals. It uses the {@link StringRepresentable}
     * method to serialize and then lookup the associated string.
     *
     * @param enumClass The enum class
     * @param <E> The enum type
     * @return An {@link EnumCodec}
     */
    static <E extends Enum<E> & StringRepresentable> MinecraftCodecable<E> stringRepresentableEnum(final Class<E> enumClass) {
        return stringRepresentableEnum(enumClass, MinecraftUtil.stringToEnum(enumClass));
    }

    /**
     * Constructs an enum codec that, when compressed, will write
     * using the associated ordinals. It uses the {@link StringRepresentable}
     * method to serialize and then lookup the associated string.
     *
     * @param enumClass The enum class
     * @param fromString A function to get an enum from a string
     * @param <E> The enum type
     * @return An {@link EnumCodec}
     */
    static <E extends Enum<E> & StringRepresentable> MinecraftCodecable<E> stringRepresentableEnum(final Class<E> enumClass, final CallableFunction<? super String, ? extends E> fromString) {
        return wrap(Codecable.enumCodec(enumClass, fromString, StringRepresentable::getSerializedName));
    }
}
