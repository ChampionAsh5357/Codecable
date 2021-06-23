/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.ashwork.functionality.callable.CallableFunction;
import net.ashwork.functionality.callable.CallableIntFunction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * All tests associated with {@link Codecable}.
 */
public final class CodecableTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link Codecable#set(Codec)}</li>
     *     <li>{@link Codecable#strictSet(Codec)}</li>
     * </ul>
     */
    @Test
    public void set() {
        JsonArray array = new JsonArray();
        GenerationUtil.generateDynamicIntSet(20, 0, 100).forEach(array::add);
        array.add(GenerationUtil.selectRandom(array));

        Codec<Set<Integer>> intSetCodec = Codecable.wrap(Codec.INT).setOf();
        TestUtil.checkDecodeJsonTest(intSetCodec, array, false);

        Codec<Set<Integer>> intStrictSetCodec = Codecable.wrap(Codec.INT).strictSetOf();
        TestUtil.checkDecodeJsonTest(intStrictSetCodec, array, true);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link Codecable#enumCodec(Class)}</li>
     *     <li>{@link Codecable#enumCodec(Class, CallableFunction, Function)}</li>
     *     <li>{@link Codecable#enumCodec(Class, CallableFunction, Function, CallableIntFunction, ToIntFunction)}</li>
     * </ul>
     */
    @Test
    public void enumCodec() {
        Codec<TestEnum> codec = Codecable.enumCodec(TestEnum.class);
        TestEnum test = GenerationUtil.selectRandom(TestEnum.values());
        TestUtil.codecJsonTest(codec, test, new JsonPrimitive(test.name()));
        TestUtil.codecCompressedJsonTest(codec, test, new JsonPrimitive(test.ordinal()));
        TestUtil.checkDecodeJsonTest(codec, new JsonPrimitive("illegal_value"), true);
        TestUtil.checkDecodeCompressedJsonTest(codec, new JsonPrimitive(-2), true);

        Codec<TestEnum> prettyCodec = Codecable.enumCodec(TestEnum.class, TestEnum::byName, TestEnum::getTestName, TestEnum::byId, TestEnum::getTestId);
        TestUtil.codecJsonTest(prettyCodec, test, new JsonPrimitive(test.getTestName()));
        TestUtil.codecCompressedJsonTest(prettyCodec, test, new JsonPrimitive(test.getTestId()));
        TestUtil.checkDecodeJsonTest(prettyCodec, new JsonPrimitive("illegal_value"), true);
        TestUtil.checkDecodeCompressedJsonTest(prettyCodec, new JsonPrimitive(-2), true);
    }

    private enum TestEnum {
        TEST1("test_one", 4),
        TEST2("test_two", 10),
        TEST3("test_three", 201),
        TEST4("test_four", -1);

        private final String name;
        private final int id;

        TestEnum(final String name, final int id) {
            this.name = name;
            this.id = id;
        }

        public String getTestName() {
            return this.name;
        }

        public int getTestId() {
            return this.id;
        }

        public static TestEnum byName(final String name) {
            return Arrays.stream(TestEnum.values())
                    .filter(e -> e.getTestName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }

        public static TestEnum byId(final int id) {
            return Arrays.stream(TestEnum.values())
                    .filter(e -> e.getTestId() == id)
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
        }
    }
}
