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
import net.minecraft.util.BitStorage;

import java.util.stream.LongStream;

/**
 * Codecs for classes within {@code net.minecraft.util}.
 */
public final class UtilCodecs {

    /**
     * A {@link BitStorage} codec. Uses a long stream to store the array.
     */
    public static final Codec<BitStorage> BIT_STORAGE = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.intRange(1, 32).fieldOf("bits").forGetter(BitStorage::getBits),
                    Codec.INT.fieldOf("size").forGetter(BitStorage::getSize),
                    Codec.LONG_STREAM.xmap(LongStream::toArray, LongStream::of).fieldOf("data").forGetter(BitStorage::getRaw)
            ).apply(instance, BitStorage::new)
    );
}
