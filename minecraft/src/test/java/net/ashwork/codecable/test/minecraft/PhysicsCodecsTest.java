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
import net.ashwork.codecable.minecraft.PhysicsCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.MinecraftComparisonUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

/**
 * All tests associated with {@link PhysicsCodecs}.
 */
public final class PhysicsCodecsTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link PhysicsCodecs#VEC2}</li>
     *     <li>{@link PhysicsCodecs#PRETTY_VEC2}</li>
     * </ul>
     */
    @Test
    public void vec2() {
        Vec2 vec2 = new Vec2(GenerationUtil.generateFloat(0, 1000), GenerationUtil.generateFloat(0, 1000));

        JsonArray vec2Output = new JsonArray();
        vec2Output.add(vec2.x);
        vec2Output.add(vec2.y);
        TestUtil.codecJsonTest(PhysicsCodecs.VEC2, vec2, vec2Output, MinecraftComparisonUtil::areVec2sEqual);

        JsonObject prettyVec2Output = new JsonObject();
        prettyVec2Output.addProperty("x", vec2.x);
        prettyVec2Output.addProperty("y", vec2.y);
        TestUtil.codecJsonTest(PhysicsCodecs.PRETTY_VEC2, vec2, prettyVec2Output, MinecraftComparisonUtil::areVec2sEqual);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link PhysicsCodecs#VEC3}</li>
     *     <li>{@link PhysicsCodecs#PRETTY_VEC3}</li>
     * </ul>
     */
    @Test
    public void vec3() {
        Vec3 vec3 = new Vec3(GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000));

        JsonArray vec3Output = new JsonArray();
        vec3Output.add(vec3.x());
        vec3Output.add(vec3.y());
        vec3Output.add(vec3.z());
        TestUtil.codecJsonTest(PhysicsCodecs.VEC3, vec3, vec3Output);

        JsonObject prettyVec3Output = new JsonObject();
        prettyVec3Output.addProperty("x", vec3.x());
        prettyVec3Output.addProperty("y", vec3.y());
        prettyVec3Output.addProperty("z", vec3.z());
        TestUtil.codecJsonTest(PhysicsCodecs.PRETTY_VEC3, vec3, prettyVec3Output);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link PhysicsCodecs#AABB}</li>
     *     <li>{@link PhysicsCodecs#PRETTY_AABB}</li>
     *     <li>{@link PhysicsCodecs#LIST_AABB}</li>
     * </ul>
     */
    @Test
    public void aabb() {
        AABB aabb = new AABB(GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000), GenerationUtil.generateDouble(0, 1000));

        JsonObject aabbOutput = new JsonObject();
        JsonArray aabbMin = new JsonArray();
        aabbMin.add(aabb.minX);
        aabbMin.add(aabb.minY);
        aabbMin.add(aabb.minZ);
        aabbOutput.add("min", aabbMin);
        JsonArray aabbMax = new JsonArray();
        aabbMax.add(aabb.maxX);
        aabbMax.add(aabb.maxY);
        aabbMax.add(aabb.maxZ);
        aabbOutput.add("max", aabbMax);
        TestUtil.codecJsonTest(PhysicsCodecs.AABB, aabb, aabbOutput);

        JsonObject prettyAABBOutput = new JsonObject();
        prettyAABBOutput.addProperty("minX", aabb.minX);
        prettyAABBOutput.addProperty("minY", aabb.minY);
        prettyAABBOutput.addProperty("minZ", aabb.minZ);
        prettyAABBOutput.addProperty("maxX", aabb.maxX);
        prettyAABBOutput.addProperty("maxY", aabb.maxY);
        prettyAABBOutput.addProperty("maxZ", aabb.maxZ);
        TestUtil.codecJsonTest(PhysicsCodecs.PRETTY_AABB, aabb, prettyAABBOutput);

        JsonArray listAABBOutput = new JsonArray();
        listAABBOutput.add(aabb.minX);
        listAABBOutput.add(aabb.minY);
        listAABBOutput.add(aabb.minZ);
        listAABBOutput.add(aabb.maxX);
        listAABBOutput.add(aabb.maxY);
        listAABBOutput.add(aabb.maxZ);
        TestUtil.codecJsonTest(PhysicsCodecs.LIST_AABB, aabb, listAABBOutput);
    }
}
