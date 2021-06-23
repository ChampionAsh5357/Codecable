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
import net.ashwork.codecable.CodecableWrapper;

/**
 * A wrapper around codecs for ease of use with Codecable
 * in Minecraft.
 *
 * @param <A> The element type
 */
public class MinecraftCodecableWrapper<A> extends CodecableWrapper<A> implements MinecraftCodecable<A> {

    public MinecraftCodecableWrapper(final Codec<A> wrappedCodec) {
        super(wrappedCodec);
    }
}
