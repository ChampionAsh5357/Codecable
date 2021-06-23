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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Codecs for classes within {@code net.minecraft.world.level.levelgen}.
 */
public final class LevelGenCodecs {

    /**
     * A {@link BoundingBox} codec. Splits the list into two groups of three, the first
     * representing the minimum values, the second the maximum.
     */
    public static final Codec<BoundingBox> BOUNDING_BOX = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("min").forGetter(box -> new BlockPos(box.minX(), box.minY(), box.minZ())),
                    BlockPos.CODEC.fieldOf("max").forGetter(box -> new BlockPos(box.maxX(), box.maxY(), box.maxZ()))
            ).apply(instance, (minPos, maxPos) -> new BoundingBox(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ()))
    );

    /**
     * A {@link BoundingBox} codec. Pretty prints all fields to the full object instead of
     * multiple lists.
     */
    public static final Codec<BoundingBox> PRETTY_BOUNDING_BOX = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("minX").forGetter(BoundingBox::minX),
                    Codec.INT.fieldOf("minY").forGetter(BoundingBox::minY),
                    Codec.INT.fieldOf("minZ").forGetter(BoundingBox::minZ),
                    Codec.INT.fieldOf("maxX").forGetter(BoundingBox::maxX),
                    Codec.INT.fieldOf("maxY").forGetter(BoundingBox::maxY),
                    Codec.INT.fieldOf("maxZ").forGetter(BoundingBox::maxZ)
            ).apply(instance, BoundingBox::new)
    );
}
