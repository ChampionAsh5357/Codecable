/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.map;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.map.base.BaseBiMapCodec;

import java.util.Objects;

/**
 * A codec for a bimap. Provides additional configurations for how the codec
 * should behave when a duplicate value is encountered or what to do when an
 * entry fails to decode.
 *
 * @param keyCodec a codec for the keys of the bimap
 * @param valueCodec a codec for the values of the bimap
 * @param failOnDuplicate if {@code true}, any duplicate values will return an
 *                        errored {@link DataResult}
 * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
 *                           bimap as soon as an error is found. The bimap will
 *                           appear in the partial when the codec errors
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public record UnboundedBiMapCodec<K, V>(Codec<K> keyCodec,
                                        Codec<V> valueCodec,
                                        boolean failOnDuplicate,
                                        boolean stopOnFirstFailure) implements BaseBiMapCodec<K, V>, Codecable<BiMap<K, V>> {

    @Override
    public <T> DataResult<Pair<BiMap<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable())
                .flatMap(map -> this.decode(ops, map, this.failOnDuplicate, this.stopOnFirstFailure)).map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(final BiMap<K, V> input, final DynamicOps<T> ops, final T prefix) {
        return this.encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final UnboundedBiMapCodec<?, ?> that = (UnboundedBiMapCodec<?, ?>) o;
        return Objects.equals(this.keyCodec, that.keyCodec) && Objects.equals(this.valueCodec, that.valueCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.keyCodec, this.valueCodec);
    }

    @Override
    public String toString() {
        return "UnboundedImprovedMapCodec[" + this.keyCodec + " -> " + this.valueCodec + ']';
    }
}
