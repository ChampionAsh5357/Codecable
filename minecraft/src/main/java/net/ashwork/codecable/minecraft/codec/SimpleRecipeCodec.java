/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft.codec;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Function;

/**
 * A base implementation of a {@link RecipeCodec}.
 *
 * @param <T> The recipe type
 */
public final class SimpleRecipeCodec<T extends Recipe<?>> implements RecipeCodec<T> {

    private final Function<ResourceLocation, Codec<T>> codec, networkCodec;

    /**
     * Constructs a codec serializer with the same file and network codec.
     *
     * @param codec The base codec used for both reading/writing to file and sending across the network
     */
    public SimpleRecipeCodec(final Function<ResourceLocation, Codec<T>> codec) {
        this(codec, codec);
    }

    public SimpleRecipeCodec(final Function<ResourceLocation, Codec<T>> codec, final Function<ResourceLocation, Codec<T>> networkCodec) {
        this.codec = codec;
        this.networkCodec = networkCodec;
    }

    @Override
    public Codec<T> codec(ResourceLocation location) {
        return this.codec.apply(location);
    }

    @Override
    public Codec<T> networkCodec(ResourceLocation location) {
        return this.networkCodec.apply(location);
    }
}
