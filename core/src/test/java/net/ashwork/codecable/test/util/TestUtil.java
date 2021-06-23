/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

/**
 * A utility for creating tests.
 */
public final class TestUtil {

    /**
     * Creates a codec test to see if the compressed json
     * element is the same as the input.
     *
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedFormat The expected format of the serialized object
     * @param <A> The original type
     * @see #codecJsonTest(Codec, Object, JsonElement, BiPredicate)
     */
    public static <A> void codecCompressedJsonTest(final Codec<A> codec, final A input, final JsonElement expectedFormat) {
        codecCompressedJsonTest(codec, input, expectedFormat, null);
    }

    /**
     * Creates a codec test to see if the compressed json
     * element is the same as the input.
     *
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedFormat The expected format of the serialized object
     * @param isEqual Checks whether the two objects are equal if not null
     * @param <A> The original type
     * @see #codecTest(DynamicOps, Codec, Object, Object, BiPredicate)
     */
    public static <A> void codecCompressedJsonTest(final Codec<A> codec, final A input, final JsonElement expectedFormat, @Nullable final BiPredicate<A, A> isEqual) {
        codecTest(JsonOps.COMPRESSED, codec, input, expectedFormat, isEqual);
    }

    /**
     * Creates a codec test to see if the json
     * element is the same as the input.
     *
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedFormat The expected format of the serialized object
     * @param <A> The original type
     * @see #codecJsonTest(Codec, Object, JsonElement, BiPredicate)
     */
    public static <A> void codecJsonTest(final Codec<A> codec, final A input, final JsonElement expectedFormat) {
        codecJsonTest(codec, input, expectedFormat, null);
    }

    /**
     * Creates a codec test to see if the json
     * element is the same as the input.
     *
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedFormat The expected format of the serialized object
     * @param isEqual Checks whether the two objects are equal if not null
     * @param <A> The original type
     * @see #codecTest(DynamicOps, Codec, Object, Object, BiPredicate)
     */
    public static <A> void codecJsonTest(final Codec<A> codec, final A input, final JsonElement expectedFormat, @Nullable final BiPredicate<A, A> isEqual) {
        codecTest(JsonOps.INSTANCE, codec, input, expectedFormat, isEqual);
    }

    /**
     * Tries to decode some input from a compressed json and checks
     * whether it succeeded or failed.
     *
     * @param codec The codec being tested
     * @param input The input being checked
     * @param checkError True if the errored state should be checked,
     *                   false if the successful state should be checked
     * @param <A> The original type
     */
    public static <A> void checkDecodeCompressedJsonTest(final Codec<A> codec, final JsonElement input, final boolean checkError) {
        checkDecodeTest(JsonOps.COMPRESSED, codec, input, checkError);
    }

    /**
     * Tries to decode some input from a json and checks whether
     * it succeeded or failed.
     *
     * @param codec The codec being tested
     * @param input The input being checked
     * @param checkError True if the errored state should be checked,
     *                   false if the successful state should be checked
     * @param <A> The original type
     */
    public static <A> void checkDecodeJsonTest(final Codec<A> codec, final JsonElement input, final boolean checkError) {
        checkDecodeTest(JsonOps.INSTANCE, codec, input, checkError);
    }

    /**
     * Creates a codec test to see if the serialized
     * object is the same as the input.
     *
     * @param ops The dynamic format used as the type
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedFormat The expected format of the serialized object
     * @param isEqual Checks whether the two objects are equal if not null
     * @param <A> The original type
     * @param <T> The format file type
     * @see #encodeTest(DynamicOps, Codec, Object, Object, BiPredicate)
     * @see #decodeTest(DynamicOps, Codec, Object, Object, BiPredicate)
     */
    public static <A, T> void codecTest(final DynamicOps<T> ops, final Codec<A> codec, final A input, final T expectedFormat, @Nullable final BiPredicate<A, A> isEqual) {
        T encoded = TestUtil.encodeTest(ops, codec, input, expectedFormat, null);
        TestUtil.decodeTest(ops, codec, encoded, input, isEqual);
    }

    /**
     * Creates a encode test to see if the serialized input
     * object is the same as the expected value.
     *
     * @param ops The dynamic format used as the type
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedOutput The formatted output to test against if not null
     * @param isEqual Checks whether the two objects are equal if not null
     * @param <A> The original type
     * @param <T> The format file type
     * @return The encoded object
     * @see CodecUtil#encode(DynamicOps, Codec, Object)
     */
    public static <A, T> T encodeTest(final DynamicOps<T> ops, final Codec<A> codec, A input, final T expectedOutput, @Nullable final BiPredicate<T, T> isEqual) {
        T encoded = CodecUtil.encode(ops, codec, input);
        if (isEqual != null) Assertions.assertTrue(isEqual.test(expectedOutput, encoded));
        else Assertions.assertEquals(expectedOutput, encoded);
        return encoded;
    }

    /**
     * Creates a codec test to see if the deserialized
     * object is the same as the expected value.
     *
     * @param ops The dynamic format used as the type
     * @param codec The codec being tested
     * @param input The input being checked
     * @param expectedOutput The formatted output to test against if not null
     * @param isEqual Checks whether the two objects are equal if not null
     * @param <A> The original type
     * @param <T> The format file type
     * @return The decoded object
     * @see CodecUtil#decode(DynamicOps, Codec, Object)
     */
    public static <A, T> A decodeTest(final DynamicOps<T> ops, final Codec<A> codec, T input, final A expectedOutput, @Nullable final BiPredicate<A, A> isEqual) {
        A decoded = CodecUtil.decode(ops, codec, input);
        if (isEqual != null) Assertions.assertTrue(isEqual.test(expectedOutput, decoded));
        else Assertions.assertEquals(expectedOutput, decoded);
        return decoded;
    }

    /**
     * Tries to decode some input and checks whether it succeeded
     * or failed.
     *
     * @param ops The dynamic format used as the type
     * @param codec The codec being tested
     * @param input The input being checked
     * @param checkError True if the errored state should be checked,
     *                   false if the successful state should be checked
     * @param <A> The original type
     * @param <T> The file format type
     */
    public static <A, T> void checkDecodeTest(final DynamicOps<T> ops, final Codec<A> codec, final T input, final boolean checkError) {
        boolean hasSucceeded = CodecUtil.canDecode(ops, codec, input);
        if (checkError) Assertions.assertFalse(hasSucceeded);
        else Assertions.assertTrue(hasSucceeded);
    }
}
