/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.function.DataResultFunction;
import net.ashwork.mc.codecable.primitive.MinecraftWrapperCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

/**
 * An extension of {@link Codecable} and its operations.
 *
 * @param <A> the type of the object
 */
public interface MinecraftCodecable<A> extends Codecable<A> {

    /**
     * Wraps a codec to make it an instance of {@code MinecraftCodecable}.
     *
     * @param codec the codec delegate to wrap
     * @param <A> the type of the object
     * @return a wrapped codec
     */
    static <A> MinecraftCodecable<A> wrap(final Codec<A> codec) {
        return new MinecraftWrapperCodec<>(codec);
    }

    /**
     * A mutable component codec which uses JSON serialization to decode and encode
     * the data.
     * 
     * @see Component.Serializer#fromJson(JsonElement)
     * @see Component.Serializer#toJsonTree(Component)
     */
    MinecraftCodecable<MutableComponent> MUTABLE_COMPONENT = wrap(Codec.PASSTHROUGH.flatXmap(
            new DataResultFunction<>(d -> Optional.ofNullable(
                    Component.Serializer.fromJson(d.convert(JsonOps.INSTANCE).getValue())
            ).orElseThrow(() -> new IllegalStateException("Not a valid mutable component"))),
            new DataResultFunction<>(c -> new Dynamic<>(JsonOps.INSTANCE, Component.Serializer.toJsonTree(c)))
    ));

    /**
     * An ingredient codec which uses JSON serialization to decode and encode the
     * data.
     *
     * @see Ingredient#fromJson(JsonElement)
     * @see Ingredient#toJson()
     */
    MinecraftCodecable<Ingredient> INGREDIENT = wrap(Codec.PASSTHROUGH.flatXmap(
            new DataResultFunction<>(d -> Ingredient.fromJson(d.convert(JsonOps.INSTANCE).getValue())),
            new DataResultFunction<>(i -> new Dynamic<>(JsonOps.INSTANCE, i.toJson()))
    ));
}
