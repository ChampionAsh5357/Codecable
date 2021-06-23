/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Codecs for classes within {@code net.minecraft.world.phys}.
 */
public final class PhysicsCodecs {

    /**
     * A {@link Vec2} codec. Follows the default format for vectors.
     */
    public static final Codec<Vec2> VEC2 = Codec.FLOAT.listOf().comapFlatMap(list ->
                    Util.fixedSize(list, 2).map(l -> new Vec2(l.get(0), l.get(1)))
            , vec -> ImmutableList.of(vec.x, vec.y));

    /**
     * A {@link Vec2} codec. Pretty prints using an object instead of a list.
     */
    public static final Codec<Vec2> PRETTY_VEC2 = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(vec -> vec.x),
                    Codec.FLOAT.fieldOf("y").forGetter(vec -> vec.y)
            ).apply(instance, Vec2::new)
    );

    /**
     * A {@link Vec3} codec. Follows the default format for vectors.
     */
    public static final Codec<Vec3> VEC3 = Codec.DOUBLE.listOf().comapFlatMap(list ->
                    Util.fixedSize(list, 3).map(l -> new Vec3(l.get(0), l.get(1), l.get(2)))
            , vec -> ImmutableList.of(vec.x(), vec.y(), vec.z()));

    /**
     * A {@link Vec3} codec. Pretty prints using an object instead of a list.
     */
    public static final Codec<Vec3> PRETTY_VEC3 = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
                    Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
                    Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
            ).apply(instance, Vec3::new)
    );

    /**
     * An {@link AABB} codec. Splits the list into two groups of three, the first
     * representing the minimum values, the second the maximum.
     */
    public static final Codec<AABB> AABB = RecordCodecBuilder.create(instance ->
            instance.group(
                    VEC3.fieldOf("min").forGetter(aabb -> new Vec3(aabb.minX, aabb.minY, aabb.minZ)),
                    VEC3.fieldOf("max").forGetter(aabb -> new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ))
            ).apply(instance, AABB::new)
    );

    /**
     * An {@link AABB} codec. Pretty prints all fields to the full object instead of
     * multiple lists.
     */
    public static final Codec<AABB> PRETTY_AABB = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("minX").forGetter(aabb -> aabb.minX),
                    Codec.DOUBLE.fieldOf("minY").forGetter(aabb -> aabb.minY),
                    Codec.DOUBLE.fieldOf("minZ").forGetter(aabb -> aabb.minZ),
                    Codec.DOUBLE.fieldOf("maxX").forGetter(aabb -> aabb.maxX),
                    Codec.DOUBLE.fieldOf("maxY").forGetter(aabb -> aabb.maxY),
                    Codec.DOUBLE.fieldOf("maxZ").forGetter(aabb -> aabb.maxZ)
            ).apply(instance, AABB::new)
    );

    /**
     * An {@link AABB} codec. Stores the data in a single long list instead of split
     * between two.
     */
    public static final Codec<AABB> LIST_AABB = Codec.DOUBLE.listOf().comapFlatMap(list ->
                    Util.fixedSize(list, 6).map(l -> new AABB(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5)))
            , aabb -> ImmutableList.of(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ));
}
