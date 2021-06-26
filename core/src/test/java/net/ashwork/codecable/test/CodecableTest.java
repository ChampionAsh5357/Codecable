/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.test.util.ComparisonUtil;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.ashwork.functionality.callable.CallableFunction;
import net.ashwork.functionality.callable.CallableIntFunction;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

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

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link Codecable#iterator(Codec)}</li>
     *     <li>{@link Codecable#enumeration(Codec)}</li>
     * </ul>
     */
    @Test
    public void iterations() {
        List<Integer> ints = Lists.newArrayList(Arrays.stream(GenerationUtil.generateDynamicIntArray(20, 0, 1000)).boxed().toArray(Integer[]::new));
        JsonArray array = new JsonArray();
        ints.forEach(array::add);

        Codec<Iterator<Integer>> iterator = Codecable.wrap(Codec.INT).iteratorOf();
        TestUtil.codecJsonTest(iterator, ints.iterator(), array, (exp, act) -> ComparisonUtil.areIteratorsEqual(ints.iterator(), act, Objects::equals));

        Codec<Enumeration<Integer>> enumeration = Codecable.wrap(Codec.INT).enumerationOf();
        TestUtil.codecJsonTest(enumeration, Iterators.asEnumeration(ints.iterator()), array, (exp, act) -> ComparisonUtil.areEnumerationsEqual(Iterators.asEnumeration(ints.iterator()), act, Objects::equals));
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link Codecable#keyListMapCodec(MapCodec, MapCodec)}</li>
     *     <li>{@link Codecable#listMapCodec(MapCodec, MapCodec)}</li>
     * </ul>
     */
    @Test
    public void listMaps() throws Throwable {
        Map<Integer, Integer> ints = GenerationUtil.generateDynamicIntSet(50, 0, 1000).stream().collect(Collectors.toMap(Function.identity(), i -> GenerationUtil.generateInt(0, 3)));
        Method deepCopyMethod = JsonArray.class.getDeclaredMethod("deepCopy");
        deepCopyMethod.setAccessible(true);
        MethodHandle deepCopy = MethodHandles.lookup().unreflect(deepCopyMethod);

        Codec<Map<Integer, Integer>> keyList = Codecable.keyListMapCodec(Codec.INT.listOf().fieldOf("keys"), Codec.INT.fieldOf("value"));
        JsonArray keyListArray = new JsonArray();
        Map<Integer, JsonArray> inverse = new HashMap<>();
        ints.forEach((key, value) -> inverse.computeIfAbsent(value, v -> new JsonArray()).add(key));
        inverse.forEach((i, array) -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("value", i);
            obj.add("keys", array);
            keyListArray.add(obj);
        });
        TestUtil.codecJsonTest(keyList, ints, keyListArray);

        JsonArray keyListDuplicates = (JsonArray) deepCopy.invokeExact(keyListArray);
        JsonArray randomKeyListValue = ((JsonArray) ((JsonObject) GenerationUtil.selectRandom(keyListDuplicates)).get("keys"));
        randomKeyListValue.add(GenerationUtil.selectRandom(randomKeyListValue));
        TestUtil.checkDecodeJsonTest(keyList, keyListDuplicates, true);

        Codec<Map<Integer, Integer>> list = Codecable.listMapCodec(Codec.INT.fieldOf("key"), Codec.INT.fieldOf("value"));
        JsonArray listArray = new JsonArray();
        ints.forEach((key, value) -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("value", value);
            obj.addProperty("key", key);
            listArray.add(obj);
        });
        TestUtil.codecJsonTest(list, ints, listArray);

        JsonArray listDuplicates = (JsonArray) deepCopy.invokeExact(keyListArray);
        listDuplicates.add(GenerationUtil.selectRandom(listDuplicates));
        TestUtil.checkDecodeJsonTest(list, listDuplicates, true);
    }

    /**
     * A test enum for use with this class.
     */
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

        /**
         * @return Gets the name associated with the enum entry
         */
        public String getTestName() {
            return this.name;
        }

        /**
         * @return Gets the id associated with the enum entry
         */
        public int getTestId() {
            return this.id;
        }

        /**
         * Grabs the enum using the name or null if not present.
         *
         * @param name The name within the enum
         * @return The {@link TestEnum} or null
         */
        public static TestEnum byName(final String name) {
            return Arrays.stream(TestEnum.values())
                    .filter(e -> e.getTestName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }

        /**
         * Grabs the enum using the id or throws an exception if not present.
         *
         * @param id The id within the enum
         * @return The {@link TestEnum}
         * @throws RuntimeException If the enum does not exist
         */
        public static TestEnum byId(final int id) {
            return Arrays.stream(TestEnum.values())
                    .filter(e -> e.getTestId() == id)
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
        }
    }
}
