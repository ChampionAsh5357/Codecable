/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.SimpleMapCodec;
import net.ashwork.codecable.map.base.BaseImprovedMapCodec;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * An improved implementation of {@link SimpleMapCodec} for creating a key-compressible
 * map {@link MapCodec}. Provides additional configurations on what to do when an entry
 * fails to decode.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 *
 * @see SimpleMapCodec
 */
public final class SimpleImprovedMapCodec<K, V> extends MapCodec<Map<K, V>> implements BaseImprovedMapCodec<K, V> {

    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Keyable keys;
    private final boolean stopOnFirstFailure;

    /**
     * Default constructor.
     *
     * @param keyCodec a codec for the keys of the map
     * @param valueCodec a codec for the values of the map
     * @param keys a list of keys that this map can have
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           map as soon as an error is found. The map will
     *                           appear in the partial when the codec errors
     */
    public SimpleImprovedMapCodec(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys, final boolean stopOnFirstFailure) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.keys = keys;
        this.stopOnFirstFailure = stopOnFirstFailure;
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return this.keys.keys(ops);
    }

    @Override
    public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        return BaseImprovedMapCodec.super.decode(ops, input, this.stopOnFirstFailure);
    }

    @Override
    public Codec<K> keyCodec() {
        return this.keyCodec;
    }

    @Override
    public Codec<V> valueCodec() {
        return this.valueCodec;
    }

    @Override
    public <T> RecordBuilder<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        return BaseImprovedMapCodec.super.encode(input, ops, prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final SimpleImprovedMapCodec<?, ?> that = (SimpleImprovedMapCodec<?, ?>) o;
        return Objects.equals(this.keyCodec, that.keyCodec) && Objects.equals(this.valueCodec, that.valueCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.keyCodec, this.valueCodec);
    }

    @Override
    public String toString() {
        return "SimpleImprovedMapCodec[" + this.keyCodec + " -> " + this.valueCodec + ']';
    }
}
