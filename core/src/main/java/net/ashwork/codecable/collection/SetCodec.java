/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.util.MapHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A codec for a set. Provides additional configurations for how the codec should
 * behave when a duplicate is encountered or what to do when an element fails to
 * decode.
 *
 * @param elementCodec a codec for the elements of the set
 * @param failOnDuplicate if {@code true}, any duplicate elements will return an
 *                        errored {@link DataResult}
 * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
 *                           set as soon as an error is found. The set will
 *                           appear in the partial when the codec errors
 * @param <A> the type of the element
 */
public record SetCodec<A>(Codec<A> elementCodec, boolean failOnDuplicate,
                          boolean stopOnFirstFailure) implements Codecable<Set<A>> {

    @Override
    public <T> DataResult<Pair<Set<A>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(list -> {
            // Construct data result holders
            final ImmutableSet.Builder<A> read = ImmutableSet.builder();
            final Set<A> elements = new HashSet<>();

            // Construct failure information
            final ImmutableList.Builder<T> failed = ImmutableList.builder(),
                    notRead = ImmutableList.builder(),
                    duplicates = ImmutableList.builder();

            // Get modifier instances
            final AtomicBoolean shouldStop = new AtomicBoolean();
            final AtomicReference<DataResult<Unit>> result = new AtomicReference<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            // For each element in list
            list.accept(element -> {
                // If no other data should be parsed after failure, just return the instance
                if (shouldStop.getPlain()) {
                    notRead.add(element);
                    return;
                }
                final AtomicBoolean normalFail = new AtomicBoolean(true);

                // Decode element
                final DataResult<Pair<A, T>> e = this.elementCodec.decode(ops, element).flatMap(p -> {
                    // Check if element already added
                    if (!elements.add(p.getFirst())) {
                        duplicates.add(element);
                        normalFail.setPlain(false);
                        if (failOnDuplicate) return DataResult.error("Duplicate element: " + element, p);
                    }
                    return DataResult.success(p);
                });
                e.get().mapBoth(p -> read.add(p.getFirst()), er -> {
                    if (stopOnFirstFailure) shouldStop.setPlain(true);
                    if (normalFail.getPlain()) failed.add(element);
                    return null;
                });

                result.setPlain(result.getPlain().apply2stable((r, v) -> r, e));
            });

            // Build results
            final Set<A> entries = read.build();
            final Map<String, T> errors = MapHelper.createAndAdd(ImmutableMap.of(
                    "failed inputs", (k, b) -> MapHelper.addIfNotEmpty(b, k, failed.build(), c -> ops.createList(c.stream())),
                    "duplicates", (k, b) -> MapHelper.addIfNotEmpty(b, k, duplicates.build(), c -> ops.createList(c.stream())),
                    "unread inputs", (k, b) -> MapHelper.addIfNotEmpty(b, k, notRead.build(), c -> ops.createList(c.stream()))
            ));
            final Pair<Set<A>, T> pair = Pair.of(entries, errors.getOrDefault("failed inputs", ops.emptyList()));

            return result.getPlain().map(unit -> pair).setPartial(pair).mapError(e -> e + MapHelper.toString(errors));
        });
    }

    @Override
    public <T> DataResult<T> encode(final Set<A> input, final DynamicOps<T> ops, final T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();
        input.forEach(a -> builder.add(this.elementCodec.encodeStart(ops, a)));
        return builder.build(prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final SetCodec<?> that = (SetCodec<?>) o;
        return Objects.equals(this.elementCodec, that.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.elementCodec);
    }

    @Override
    public String toString() {
        return "SetCodec[" + this.elementCodec + ']';
    }
}
