/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.ashwork.codecable.minecraft.MinecraftCodecable;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.MinecraftComparisonUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.ashwork.functionality.callable.CallableFunction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * All tests associated with {@link MinecraftCodecable}.
 */
public final class MinecraftCodecableTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link MinecraftCodecable#tuple(Codec, Codec)}</li>
     *     <li>{@link MinecraftCodecable#mapTuple(MapCodec, MapCodec)}</li>
     * </ul>
     */
    @Test
    public void tuple() {
        Tuple<Integer, Double> tuple = new Tuple<>(GenerationUtil.generateInt(0, 10000), GenerationUtil.generateDouble(0, 10000));
        Codec<Tuple<Integer, Double>> codec = MinecraftCodecable.tuple(Codec.INT.fieldOf("i").codec(), Codec.DOUBLE.fieldOf("d").codec());
        Codec<Tuple<Integer, Double>> mapCodec = MinecraftCodecable.mapTuple(Codec.INT.fieldOf("i"), Codec.DOUBLE.fieldOf("d")).codec();
        JsonObject obj = new JsonObject();
        obj.addProperty("i", tuple.getA());
        obj.addProperty("d", tuple.getB());
        TestUtil.codecJsonTest(codec, tuple, obj, (expected, actual) -> MinecraftComparisonUtil.areTuplesEqual(expected, actual, Objects::equals, Objects::equals));
        TestUtil.codecJsonTest(mapCodec, tuple, obj, (expected, actual) -> MinecraftComparisonUtil.areTuplesEqual(expected, actual, Objects::equals, Objects::equals));
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link MinecraftCodecable#stringRepresentableEnum(Class)}</li>
     *     <li>{@link MinecraftCodecable#stringRepresentableEnum(Class, CallableFunction)}</li>
     * </ul>
     */
    @Test
    public void stringRepresentableEnum() {
        Codec<TestEnum> codec = MinecraftCodecable.stringRepresentableEnum(TestEnum.class);
        Codec<TestEnum> nameCodec = MinecraftCodecable.stringRepresentableEnum(TestEnum.class, TestEnum::byName);
        TestEnum test = GenerationUtil.selectRandom(TestEnum.values());
        JsonPrimitive primitive = new JsonPrimitive(test.getSerializedName());
        TestUtil.codecJsonTest(codec, test, primitive);
        TestUtil.codecJsonTest(nameCodec, test, primitive);
    }

    /**
     * A test enum for use with this class.
     */
    private enum TestEnum implements StringRepresentable {
        TEST1("test_one"),
        TEST2("test_two"),
        TEST3("test_three"),
        TEST4("test_four");

        private final String name;

        TestEnum(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        /**
         * Grabs the enum using the name or null if not present.
         *
         * @param name The name within the enum
         * @return The {@link TestEnum} or null
         */
        @Nullable
        public static TestEnum byName(final String name) {
            return Arrays.stream(TestEnum.values())
                    .filter(e -> e.getSerializedName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }
    }
}
