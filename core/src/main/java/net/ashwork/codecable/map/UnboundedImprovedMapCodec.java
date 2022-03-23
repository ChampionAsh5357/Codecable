/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.map;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.map.base.BaseImprovedMapCodec;

import java.util.Map;
import java.util.Objects;

/**
 * An improved implementation of {@link UnboundedMapCodec} for creating a map
 * codec. Provides additional configurations on what to do when an entry fails
 * to decode.
 *
 * @param keyCodec a codec for the keys of the map
 * @param valueCodec a codec for the values of the map
 * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
 *                           map as soon as an error is found. The map will
 *                           appear in the partial when the codec errors
 * @param <K> the type of the key
 * @param <V> the type of the value
 *
 * @see UnboundedMapCodec
 */
public record UnboundedImprovedMapCodec<K, V>(Codec<K> keyCodec,
                                              Codec<V> valueCodec,
                                              boolean stopOnFirstFailure) implements BaseImprovedMapCodec<K, V>, Codecable<Map<K, V>> {

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable())
                .flatMap(map -> this.decode(ops, map, this.stopOnFirstFailure)).map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final T prefix) {
        return this.encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final UnboundedImprovedMapCodec<?, ?> that = (UnboundedImprovedMapCodec<?, ?>) o;
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
