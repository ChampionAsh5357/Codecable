/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nullable;

/**
 * A utility for running codecs.
 */
public final class CodecUtil {

    /**
     * Encodes an object to the specified type.
     *
     * @param ops The ops to encode the object with
     * @param codec The codec used for encoding
     * @param input The input being encoded
     * @param <A> The input type
     * @param <T> The output type
     * @return The encoded output
     */
    public static <A, T> T encode(final DynamicOps<T> ops, final Codec<A> codec, final A input) {
        @Nullable T element = codec.encodeStart(ops, input).result().orElse(null);
        Assertions.assertNotNull(element);
        return element;
    }

    /**
     * Decodes a type to the specified object.
     *
     * @param ops The ops to decode the object with
     * @param codec The codec used for decoding
     * @param input The input being decoded
     * @param <A> The output type
     * @param <T> The input type
     * @return The decoded output
     */
    public static <A, T> A decode(final DynamicOps<T> ops, final Codec<A> codec, final T input) {
        @Nullable A element = codec.parse(ops, input).result().orElse(null);
        Assertions.assertNotNull(element);
        return element;
    }

    /**
     * Checks if an object can be decoded without errors.
     *
     * @param ops The ops to decode the object with
     * @param codec The codec used for decoding
     * @param input The input being decoded
     * @param <A> The output type
     * @param <T> The input type
     * @return If there are no errors during decoding
     */
    public static <A, T> boolean canDecode(final DynamicOps<T> ops, final Codec<A> codec, final T input) {
        return codec.parse(ops, input).result().isPresent();
    }
}
