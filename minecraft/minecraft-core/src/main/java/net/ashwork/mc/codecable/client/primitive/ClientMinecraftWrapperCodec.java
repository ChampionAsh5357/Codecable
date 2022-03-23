/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable.client.primitive;

import com.mojang.serialization.Codec;
import net.ashwork.mc.codecable.client.ClientMinecraftCodecable;
import net.ashwork.mc.codecable.primitive.MinecraftWrapperCodec;

/**
 * A wrapper to make a codec an instance of {@link ClientMinecraftCodecable}.
 *
 * @param <A> the type of the object
 */
public class ClientMinecraftWrapperCodec<A> extends MinecraftWrapperCodec<A> implements ClientMinecraftCodecable<A> {

    /**
     * Default constructor.
     *
     * @param delegate the wrapped codec to delegate behavior to
     */
    public ClientMinecraftWrapperCodec(final Codec<A> delegate) {
        super(delegate);
    }

    @Override
    protected String wrapperName() {
        return "ClientMinecraftWrapperCodec";
    }
}
