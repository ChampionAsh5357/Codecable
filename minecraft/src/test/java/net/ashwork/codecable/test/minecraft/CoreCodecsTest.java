/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import net.ashwork.codecable.minecraft.CoreCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.MinecraftTestUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.core.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * All tests associated with {@link CoreCodecs}.
 */
public final class CoreCodecsTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link CoreCodecs#DIRECTION}</li>
     *     <li>{@link CoreCodecs#AXIS}</li>
     *     <li>{@link CoreCodecs#FRONT_AND_TOP}</li>
     * </ul>
     */
    @Test
    public void enums() {
        MinecraftTestUtil.enumJsonTest(CoreCodecs.DIRECTION, Direction.values());
        MinecraftTestUtil.enumJsonTest(CoreCodecs.AXIS, Direction.Axis.values());
        MinecraftTestUtil.enumJsonTest(CoreCodecs.FRONT_AND_TOP, FrontAndTop.values());
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link CoreCodecs#PRETTY_BLOCK_POS}</li>
     *     <li>{@link CoreCodecs#COMPRESSED_BLOCK_POS}</li>
     * </ul>
     */
    @Test
    public void blockPos() {
        BlockPos pos = new BlockPos(GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000));

        JsonObject prettyPosOutput = new JsonObject();
        prettyPosOutput.addProperty("x", pos.getX());
        prettyPosOutput.addProperty("y", pos.getY());
        prettyPosOutput.addProperty("z", pos.getZ());
        TestUtil.codecJsonTest(CoreCodecs.PRETTY_BLOCK_POS, pos, prettyPosOutput);

        JsonPrimitive compressedPosOutput = new JsonPrimitive(pos.asLong());
        TestUtil.codecJsonTest(CoreCodecs.COMPRESSED_BLOCK_POS, pos, compressedPosOutput);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link CoreCodecs#SECTION_POS}</li>
     *     <li>{@link CoreCodecs#PRETTY_SECTION_POS}</li>
     *     <li>{@link CoreCodecs#COMPRESSED_SECTION_POS}</li>
     * </ul>
     */
    @Test
    public void sectionPos() {
        SectionPos pos = SectionPos.of(GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000));

        JsonArray posOutput = new JsonArray();
        posOutput.add(pos.getX());
        posOutput.add(pos.getY());
        posOutput.add(pos.getZ());
        TestUtil.codecJsonTest(CoreCodecs.SECTION_POS, pos, posOutput);

        JsonObject prettyPosOutput = new JsonObject();
        prettyPosOutput.addProperty("x", pos.getX());
        prettyPosOutput.addProperty("y", pos.getY());
        prettyPosOutput.addProperty("z", pos.getZ());
        TestUtil.codecJsonTest(CoreCodecs.PRETTY_SECTION_POS, pos, prettyPosOutput);

        JsonPrimitive compressedPosOutput = new JsonPrimitive(pos.asLong());
        TestUtil.codecJsonTest(CoreCodecs.COMPRESSED_SECTION_POS, pos, compressedPosOutput);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link CoreCodecs#ROTATIONS}</li>
     *     <li>{@link CoreCodecs#PRETTY_ROTATIONS}</li>
     * </ul>
     */
    @Test
    public void rotations() {
        Rotations rot = new Rotations(GenerationUtil.generateFloat(0.0F, 360.0F), GenerationUtil.generateFloat(0.0F, 360.0F), GenerationUtil.generateFloat(0.0F, 360.0F));

        JsonArray posOutput = new JsonArray();
        posOutput.add(rot.getX());
        posOutput.add(rot.getY());
        posOutput.add(rot.getZ());
        TestUtil.codecJsonTest(CoreCodecs.ROTATIONS, rot, posOutput);

        JsonObject prettyPosOutput = new JsonObject();
        prettyPosOutput.addProperty("x", rot.getX());
        prettyPosOutput.addProperty("y", rot.getY());
        prettyPosOutput.addProperty("z", rot.getZ());
        TestUtil.codecJsonTest(CoreCodecs.PRETTY_ROTATIONS, rot, prettyPosOutput);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link CoreCodecs#nonNullList(Class, Codec, Object)}</li>
     * </ul>
     */
    @Test
    public void nonNullList() {
        int defaultValue = 5;
        Codec<NonNullList<Integer>> listCodec = CoreCodecs.nonNullList(Integer.class, Codec.INT, defaultValue);
        int[] values = GenerationUtil.generateDynamicIntArray(10, 0, 30);

        // Test dynamically sized list
        NonNullList<Integer> ints = NonNullList.of(defaultValue, Arrays.stream(values).boxed().toArray(Integer[]::new));
        JsonArray intsOutput = new JsonArray();
        ints.forEach(intsOutput::add);
        TestUtil.codecJsonTest(listCodec, ints, intsOutput);

        // Test statically sized list
        NonNullList<Integer> intsSized = NonNullList.withSize(10, defaultValue);
        intsSized.set(4, 30);
        intsSized.set(9, 23);
        JsonArray intsSizedOutput = new JsonArray();
        intsSized.forEach(intsSizedOutput::add);
        TestUtil.codecJsonTest(listCodec, intsSized, intsSizedOutput);
    }
}
