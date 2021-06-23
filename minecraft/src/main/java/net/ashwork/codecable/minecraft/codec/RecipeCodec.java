/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft.codec;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.util.LoggerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * An extension of {@link RecipeSerializer} that uses
 * a codec instead of direct file type objects.
 *
 * @param <T> The recipe type
 */
public interface RecipeCodec<T extends Recipe<?>> extends RecipeSerializer<T> {

    /**
     * Constructs a codec to read/write data from a file.
     *
     * @param location The id of the recipe
     * @return The constructed recipe codec
     */
    Codec<T> codec(ResourceLocation location);

    /**
     * Constructs a codec to send information across the network.
     *
     * @param location The id of the recipe
     * @return The constructed recipe codec
     */
    Codec<T> networkCodec(ResourceLocation location);

    @Override
    default T fromJson(final ResourceLocation id, final JsonObject recipeJson) {
        return this.codec(id).parse(JsonOps.INSTANCE, recipeJson)
                .getOrThrow(true, error ->
                        LoggerUtil.LOGGER.error(LoggerUtil.RECIPE_CODEC,
                                "An error has occurred trying to read {} from the json object {}: {}",
                                id, recipeJson, error)
                );
    }

    @Override
    default T fromNetwork(final ResourceLocation id, final FriendlyByteBuf buffer) {
        return buffer.readWithCodec(this.networkCodec(id));
    }

    @Override
    default void toNetwork(final FriendlyByteBuf buffer, final T recipe) {
        buffer.writeWithCodec(this.networkCodec(recipe.getId()), recipe);
    }
}
