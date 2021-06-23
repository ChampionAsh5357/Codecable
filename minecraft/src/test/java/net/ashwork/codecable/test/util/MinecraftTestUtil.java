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
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringRepresentable;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A utility for creating tests.
 */
public final class MinecraftTestUtil {

    /**
     * Creates an enum codec test to see if the
     * json element is the same as the input.
     *
     * @param codec The codec being tested
     * @param values The enumeration values to choose from
     * @param <E> The enum type
     */
    public static <E extends Enum<E> & StringRepresentable> void enumJsonTest(final Codec<E> codec, final E[] values) {
        enumTest(JsonOps.INSTANCE, codec, values);
    }

    /**
     * Creates an enum codec test to see if the
     * serialized object is the same as the input.
     *
     * @param ops The dynamic format used as the type
     * @param codec The codec being tested
     * @param values The enumeration values to choose from
     * @param <T> The format file type
     * @param <E> The enum type
     */
    public static <T, E extends Enum<E> & StringRepresentable> void enumTest(final DynamicOps<T> ops, final Codec<E> codec, final E[] values) {
        E e = GenerationUtil.selectRandom(values);
        TestUtil.codecTest(ops, codec, e, ops.createString(e.getSerializedName()), null);
    }

    /**
     * Creates a network test to see if elements are
     * properly sent across the buffer.
     *
     * @param input The input being sent
     * @param toNetwork The method used to write to the buffer
     * @param fromNetwork The method used to read from the buffer
     * @param isEqual Checks whether the two objects are equal if not null
     * @param <A> The object type
     */
    public static <A> void networkTest(final A input, final BiConsumer<FriendlyByteBuf, A> toNetwork, final Function<FriendlyByteBuf, A> fromNetwork, @Nullable final BiPredicate<A, A> isEqual) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        toNetwork.accept(buffer, input);
        A output = fromNetwork.apply(buffer);
        if (isEqual != null) Assertions.assertTrue(isEqual.test(input, output));
        else Assertions.assertEquals(input, output);
    }
}
