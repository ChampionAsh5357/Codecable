/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable.primitive;

import com.mojang.serialization.Codec;
import net.ashwork.codecable.primitive.WrapperCodec;
import net.ashwork.mc.codecable.MinecraftCodecable;

/**
 * A wrapper to make a codec an instance of {@link MinecraftCodecable}.
 *
 * @param <A> the type of the object
 */
public class MinecraftWrapperCodec<A> extends WrapperCodec<A> implements MinecraftCodecable<A> {

    /**
     * Default constructor.
     *
     * @param delegate the wrapped codec to delegate behavior to
     */
    public MinecraftWrapperCodec(final Codec<A> delegate) {
        super(delegate);
    }

    @Override
    protected String wrapperName() {
        return "MinecraftWrapperCodec";
    }
}
