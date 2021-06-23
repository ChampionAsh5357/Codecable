/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Objects;

/**
 * A wrapper around codecs for ease of use with Codecable.
 *
 * @param <A> The element type
 */
public class CodecableWrapper<A> implements Codecable<A> {

    private final Codec<A> wrappedCodec;

    public CodecableWrapper(final Codec<A> wrappedCodec) {
        this.wrappedCodec = Objects.requireNonNull(wrappedCodec, "The wrapped codec cannot be null.");
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input) {
        return this.wrappedCodec.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(final A input, final DynamicOps<T> ops, final T prefix) {
        return this.wrappedCodec.encode(input, ops, prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final CodecableWrapper<?> that = (CodecableWrapper<?>) o;
        return this.wrappedCodec.equals(that.wrappedCodec);
    }

    @Override
    public int hashCode() {
        return this.wrappedCodec.hashCode();
    }

    @Override
    public String toString() {
        return this.wrappedCodec.toString();
    }
}
