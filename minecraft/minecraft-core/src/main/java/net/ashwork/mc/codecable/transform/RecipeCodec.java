/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable.transform;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;

/**
 * An implementation of {@link RecipeSerializer} which uses a codec to encode and
 * decode the data.
 *
 * @param <T> the type of the recipe
 */
public interface RecipeCodec<T extends Recipe<?>> extends RecipeSerializer<T> {

    /**
     * Returns the recipe codec for reading from file that does not encode or
     * decode the recipe identifier.
     *
     * @param recipeId the name of the recipe
     * @return a recipe codec
     */
    Codec<T> codec(final ResourceLocation recipeId);

    /**
     * Returns the recipe codec for sending across the network that does not
     * encode or decode the recipe identifier.
     *
     * @implNote
     * If not defined, defaults to {@link #codec(ResourceLocation)}.
     *
     * @param recipeId the name of the recipe
     * @return a recipe codec
     */
    default Codec<T> networkCodec(final ResourceLocation recipeId) {
        return this.codec(recipeId);
    }

    @Nonnull
    @Override
    default T fromJson(final @Nonnull ResourceLocation recipeId, final @Nonnull JsonObject serializedRecipe) {
        return this.codec(recipeId).parse(JsonOps.INSTANCE, serializedRecipe)
                .getOrThrow(false, e -> LogUtils.getLogger().error("Cannot read {} from {}: {}", serializedRecipe, recipeId, e));
    }

    @Nonnull
    @Override
    default T fromNetwork(final @Nonnull ResourceLocation recipeId, final FriendlyByteBuf buffer) {
        return buffer.readWithCodec(this.networkCodec(recipeId));
    }

    @Override
    default void toNetwork(final FriendlyByteBuf buffer, final @Nonnull T recipe) {
        buffer.writeWithCodec(this.networkCodec(recipe.getId()), recipe);
    }
}
