/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.map.base;

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
import com.mojang.serialization.codecs.BaseMapCodec;
import net.ashwork.codecable.util.MapHelper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An improved implementation of {@link BaseMapCodec} for encoding and decoding
 * a map. Provides additional configurations on what to do when an entry fails
 * to decode.
 *
 * @implNote
 * This is based off a <a href="https://github.com/Mojang/DataFixerUpper/pull/55">PR by alcatrazEscapee</a>
 * addressing issues that were fixed and made configurable by this implementation.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 *
 * @see BaseMapCodec
 */
public interface BaseImprovedMapCodec<K, V> {

    /**
     * Returns the codec for the map's keys.
     *
     * @return the map key codec
     */
    Codec<K> keyCodec();

    /**
     * Returns the codec for the map's values.
     *
     * @return the map value codec
     */
    Codec<V> valueCodec();

    /**
     * Decodes the formatted data into a map.
     *
     * @apiNote
     * On failure, the decoded entries will be supplied as a partial result.
     *
     * @param ops the format operations to decode from
     * @param input a map representation of the formatted data
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           map as soon as an error is found. The map will
     *                           appear in the partial when the codec errors
     * @param <T> the type of the encoded format
     * @return a {@link DataResult} containing the map on success or a partial of
     *         the map with an error message on failure
     */
    default <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input, final boolean stopOnFirstFailure) {
        // Construct data result holders
        final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
        // Construct failure information
        final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder(),
            notRead = ImmutableList.builder();

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

                    // Decode key/value
                    final DataResult<K> k = this.keyCodec().parse(ops, pair.getFirst());
                    final DataResult<V> v = this.valueCodec().parse(ops, pair.getSecond());

                    // Create entry
                    final DataResult<Pair<K, V>> entry = k.apply2stable(Pair::of, v);
                    entry.get().mapBoth(e -> read.put(e.getFirst(), e.getSecond()), p -> {
                        if (stopOnFirstFailure) shouldStop.setPlain(true);
                        failed.add(pair);
                        return null;
                    });

                    // Map result to current success/failure reference
                    return r.apply2stable((u, p) -> u, entry);
                },
                // Map results to current success/failure reference
                (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        // Build results
        final Map<K, V> entries = read.build();
        final Map<String, T> errors = MapHelper.createAndAdd(ImmutableMap.of(
                "failed inputs", (k, b) -> MapHelper.addIfNotEmpty(b, k, failed.build(), c -> ops.createMap(c.stream())),
                "unread inputs", (k, b) -> MapHelper.addIfNotEmpty(b, k, notRead.build(), c -> ops.createMap(c.stream()))
        ));

        // Map result and failure for outputs
        return result.map(u -> entries).setPartial(entries).mapError(e -> e + MapHelper.toString(errors));
    }

    /**
     * Encodes a map into a record builder.
     *
     * @param input a map
     * @param ops the format operations to encode to
     * @param prefix data that was added prior to this map
     * @param <T> the type of the encoded format
     * @return a record builder containing the map entries
     */
    default <T> RecordBuilder<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        input.forEach((k, v) -> prefix.add(this.keyCodec().encodeStart(ops, k), this.valueCodec().encodeStart(ops, v)));
        return prefix;
    }
}
