/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.map.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.ashwork.codecable.util.MapHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A base implementation for encoding and decoding a bimap. Provides additional
 * configurations for how the codec should behave when a duplicate value is
 * encountered or what to do when an entry fails to decode.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface BaseBiMapCodec<K, V> {

    /**
     * Returns the codec for the bimap's keys.
     *
     * @return the bimap key codec
     */
    Codec<K> keyCodec();

    /**
     * Returns the codec for the bimap's values.
     *
     * @return the bimap value codec
     */
    Codec<V> valueCodec();

    /**
     * Decodes the formatted data into a bimap.
     *
     * @apiNote
     * On failure, the decoded entries will be supplied as a partial result.
     *
     * @param ops the format operations to decode from
     * @param input a map representation of the formatted data
     * @param failOnDuplicate if {@code true}, any duplicate values will return an
     *                        errored {@link DataResult}
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           bimap as soon as an error is found. The bimap will
     *                           appear in the partial when the codec errors
     * @param <T> the type of the encoded format
     * @return a {@link DataResult} containing the bimap on success or a partial
     *         of the bimap with an error message on failure
     */
    default <T> DataResult<BiMap<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input, final boolean failOnDuplicate, final boolean stopOnFirstFailure) {
        // Construct data result holders
        final ImmutableBiMap.Builder<K, V> read = ImmutableBiMap.builder();

        // Construct failure information
        final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder(),
                notRead = ImmutableList.builder();
        final Map<V, Pair<T, Set<T>>> valueToKeysMap = new HashMap<>();


        // Get modifier instances
        final AtomicBoolean shouldStop = new AtomicBoolean();

        // Construct result
        final DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                (r, pair) -> {
                    // If no other data should be parsed after failure, just return the instance
                    if (shouldStop.getPlain()) {
                        notRead.add(pair);
                        return r;
                    }
                    final AtomicBoolean normalFail = new AtomicBoolean(true);

                    // Decode key/value
                    final DataResult<K> k = this.keyCodec().parse(ops, pair.getFirst());
                    final DataResult<V> v = this.valueCodec().parse(ops, pair.getSecond());

                    // Create entry
                    final DataResult<Pair<K, V>> entry = k.apply2stable(Pair::of, v).flatMap(p -> {
                        // Check if value already added
                        final Set<T> keys = valueToKeysMap.computeIfAbsent(p.getSecond(), value -> Pair.of(pair.getSecond(), new HashSet<>())).getSecond();
                        keys.add(pair.getFirst());
                        if (keys.size() > 1) {
                            normalFail.setPlain(false);
                            if (failOnDuplicate) return DataResult.error("Duplicate value: " + pair.getSecond(), p);
                        }
                        return DataResult.success(p);
                    });
                    entry.get().mapBoth(e -> {
                        if (normalFail.getPlain())
                            read.put(e.getFirst(), e.getSecond());
                        return null;
                    }, p -> {
                        if (stopOnFirstFailure) shouldStop.setPlain(true);
                        if (normalFail.getPlain()) failed.add(pair);
                        return null;
                    });

                    return r.apply2stable((u, p) -> u, entry);
                },
                (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );
        valueToKeysMap.values().removeIf(p -> p.getSecond().size() < 2);

        // Build results
        final BiMap<K, V> entries = read.build();
        final Map<String, T> errors = MapHelper.createAndAdd(ImmutableMap.of(
                "failed inputs", (k, b) -> MapHelper.addIfNotEmpty(b, k, failed.build(), c -> ops.createMap(c.stream())),
                "duplicate values", (k, b) -> MapHelper.addIfNotEmpty(b, k, valueToKeysMap.values(), c -> ops.createMap(c.stream().map(p -> p.mapSecond(s -> ops.createList(s.stream()))))),
                "unread inputs", (k, b) -> MapHelper.addIfNotEmpty(b, k, notRead.build(), c -> ops.createMap(c.stream()))
        ));

        return result.map(u -> entries).setPartial(entries).mapError(e -> e + MapHelper.toString(errors));
    }

    /**
     * Encodes a bimap into a record builder.
     *
     * @param input a bimap
     * @param ops the format operations to encode to
     * @param prefix data that was added prior to this bimap
     * @param <T> the type of the encoded format
     * @return a record builder containing the bimap entries
     */
    default <T> RecordBuilder<T> encode(final BiMap<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        input.forEach((k, v) -> prefix.add(this.keyCodec().encodeStart(ops, k), this.valueCodec().encodeStart(ops, v)));
        return prefix;
    }
}
