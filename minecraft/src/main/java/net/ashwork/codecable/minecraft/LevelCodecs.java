/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Codecs for classes within {@code net.minecraft.world.level}.
 */
public final class LevelCodecs {

    /**
     * A {@link ChunkPos} codec. Follows the default format for vectors.
     */
    public static final Codec<ChunkPos> CHUNK_POS = Codec.INT_STREAM.comapFlatMap(stream ->
                    Util.fixedSize(stream, 2).map(arr -> new ChunkPos(arr[0], arr[1])),
            pos -> IntStream.of(pos.x, pos.z));

    /**
     * A {@link ChunkPos} codec. Pretty prints using an object instead of a list.
     */
    public static final Codec<ChunkPos> PRETTY_CHUNK_POS = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(pos -> pos.x),
                    Codec.INT.fieldOf("z").forGetter(pos -> pos.z)
            ).apply(instance, ChunkPos::new)
    );

    /**
     * A {@link ChunkPos} codec. Compresses the data into a long instead of a list.
     */
    public static final Codec<ChunkPos> COMPRESSED_CHUNK_POS = Codec.LONG.xmap(ChunkPos::new, ChunkPos::toLong);

    /**
     * A {@link BlockEventData} codec. Uses the standard block position codec.
     */
    public static final Codec<BlockEventData> BLOCK_EVENT_DATA = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(BlockEventData::getPos),
                    Registry.BLOCK.fieldOf("block").forGetter(BlockEventData::getBlock),
                    Codec.INT.fieldOf("paramA").forGetter(BlockEventData::getParamA),
                    Codec.INT.fieldOf("paramB").forGetter(BlockEventData::getParamB)
            ).apply(instance, BlockEventData::new)
    );
}
