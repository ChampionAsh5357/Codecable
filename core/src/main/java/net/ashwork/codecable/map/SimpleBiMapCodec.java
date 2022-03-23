/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.map;

import com.google.common.collect.BiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.ashwork.codecable.map.base.BaseBiMapCodec;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * A {@link MapCodec} for a key-compressible bimap. Provides additional
 * configurations for how the codec should behave when a duplicate value is
 * encountered or what to do when an entry fails to decode.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public final class SimpleBiMapCodec<K, V> extends MapCodec<BiMap<K, V>> implements BaseBiMapCodec<K, V> {

    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Keyable keys;
    private final boolean failOnDuplicate, stopOnFirstFailure;

    /**
     * Default constructor.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param keys a list of keys that this bimap can have
     * @param failOnDuplicate if {@code true}, any duplicate values will return an
     *                        errored {@link DataResult}
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           bimap as soon as an error is found. The bimap will
     *                           appear in the partial when the codec errors
     */
    public SimpleBiMapCodec(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys, final boolean failOnDuplicate, final boolean stopOnFirstFailure) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.keys = keys;
        this.failOnDuplicate = failOnDuplicate;
        this.stopOnFirstFailure = stopOnFirstFailure;
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return this.keys.keys(ops);
    }

    @Override
    public <T> DataResult<BiMap<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        return BaseBiMapCodec.super.decode(ops, input, this.failOnDuplicate, this.stopOnFirstFailure);
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
    public <T> RecordBuilder<T> encode(final BiMap<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        return BaseBiMapCodec.super.encode(input, ops, prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final SimpleBiMapCodec<?, ?> that = (SimpleBiMapCodec<?, ?>) o;
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
