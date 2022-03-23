/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.primitive;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.ashwork.codecable.Codecable;

import java.util.Objects;

/**
 * A wrapper to make a codec an instance of {@link Codecable}.
 *
 * @param <A> the type of the object
 */
public class WrapperCodec<A> implements Codecable<A> {

    private final Codec<A> delegate;

    /**
     * Default constructor.
     *
     * @param delegate the wrapped codec to delegate behavior to
     */
    public WrapperCodec(final Codec<A> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input) {
        return this.delegate.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(final A input, DynamicOps<T> ops, final T prefix) {
        return this.delegate.encode(input, ops, prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final WrapperCodec<?> that = (WrapperCodec<?>) o;
        return Objects.equals(this.delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.delegate);
    }

    /**
     * Returns the name of the codec wrapper.
     *
     * @return the name of the codec wrapper
     */
    protected String wrapperName() {
        return "WrapperCodec";
    }

    @Override
    public String toString() {
        return this.wrapperName() + "[" + this.delegate + ']';
    }
}
