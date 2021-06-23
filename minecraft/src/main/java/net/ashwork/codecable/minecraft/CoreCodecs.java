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
import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Codecs for classes within {@code net.minecraft.core}.
 */
public final class CoreCodecs {

    /**
     * A {@link Direction} codec. Will encode to a string when uncompressed and an integer when compressed.
     */
    public static final Codec<Direction> DIRECTION = MinecraftCodecable.stringRepresentableEnum(Direction.class, Direction::byName);

    /**
     * An {@link Direction.Axis} codec. Will encode to a string when uncompressed and an integer when compressed.
     */
    public static final Codec<Direction.Axis> AXIS = MinecraftCodecable.stringRepresentableEnum(Direction.Axis.class, Direction.Axis::byName);

    /**
     * A {@link FrontAndTop} codec. Will encode to a string when uncompressed and an integer when compressed.
     */
    public static final Codec<FrontAndTop> FRONT_AND_TOP = MinecraftCodecable.stringRepresentableEnum(FrontAndTop.class);

    /**
     * A {@link BlockPos} codec. Pretty prints using an object instead of a list.
     */
    public static final Codec<BlockPos> PRETTY_BLOCK_POS = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(BlockPos::getX),
                    Codec.INT.fieldOf("y").forGetter(BlockPos::getY),
                    Codec.INT.fieldOf("z").forGetter(BlockPos::getZ)
            ).apply(instance, BlockPos::new)
    );

    /**
     * A {@link BlockPos} codec. Compresses the data into a long instead of a list.
     */
    public static final Codec<BlockPos> COMPRESSED_BLOCK_POS = Codec.LONG.xmap(BlockPos::of, BlockPos::asLong);

    /**
     * A {@link SectionPos} codec. Follows the default format for vectors.
     * @see net.minecraft.core.Vec3i
     */
    public static final Codec<SectionPos> SECTION_POS = Codec.INT_STREAM.comapFlatMap(stream ->
            Util.fixedSize(stream, 3).map(arr -> SectionPos.of(arr[0], arr[1], arr[2])),
            pos -> IntStream.of(pos.getX(), pos.getY(), pos.getZ()));

    /**
     * A {@link SectionPos} codec. Pretty prints using an object instead of a list.
     */
    public static final Codec<SectionPos> PRETTY_SECTION_POS = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(SectionPos::getX),
                    Codec.INT.fieldOf("y").forGetter(SectionPos::getY),
                    Codec.INT.fieldOf("z").forGetter(SectionPos::getZ)
            ).apply(instance, SectionPos::of)
    );

    /**
     * A {@link SectionPos} codec. Compresses the data into a long instead of a list.
     */
    public static final Codec<SectionPos> COMPRESSED_SECTION_POS = Codec.LONG.xmap(SectionPos::of, SectionPos::asLong);

    /**
     * A {@link SectionPos} codec. Follows the default format for vectors.
     */
    public static final Codec<Rotations> ROTATIONS = Codec.floatRange(0.0F, 360.0F).listOf().comapFlatMap(list ->
                    Util.fixedSize(list, 3).map(l -> new Rotations(l.get(0), l.get(1), l.get(2))),
            rot -> ImmutableList.of(rot.getX(), rot.getY(), rot.getZ()));

    /**
     * A {@link SectionPos} codec. Pretty prints the using an object instead of a list.
     */
    public static final Codec<Rotations> PRETTY_ROTATIONS = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(Rotations::getX),
                    Codec.FLOAT.fieldOf("y").forGetter(Rotations::getY),
                    Codec.FLOAT.fieldOf("z").forGetter(Rotations::getZ)
            ).apply(instance, Rotations::new)
    );

    /**
     * Constructs a codec representing a {@link NonNullList} of the specified object.
     *
     * @param elementClass The element class
     * @param elementCodec The codec for the elements within the list
     * @param defaultValue The default value if the element is not present, if null then no default value is used
     * @param <A> The type of the elements within the list
     * @return A {@link Codec} representing a {@link NonNullList} of the elements
     */
    public static <A> Codec<NonNullList<A>> nonNullList(Class<A> elementClass, Codec<A> elementCodec, @Nullable A defaultValue) {
        return elementCodec.listOf().xmap(list -> NonNullList.of(defaultValue, Iterables.toArray(list, elementClass)), ArrayList::new);
    }
}
