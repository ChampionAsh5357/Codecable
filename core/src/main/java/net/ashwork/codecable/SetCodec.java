/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * A codec for a set that will only accept unique entries.
 *
 * @param <A> The type of the elements within the set
 */
public final class SetCodec<A> implements Codecable<Set<A>> {

    private final Codec<A> elementCodec;

    public SetCodec(final Codec<A> elementCodec) {
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<T> encode(final Set<A> input, final DynamicOps<T> ops, final T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();

        input.forEach(a -> builder.add(this.elementCodec.encodeStart(ops, a)));
        return builder.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<Set<A>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            final HashSet<A> read = new HashSet<>();
            final Stream.Builder<T> failed = Stream.builder();
            final AtomicReference<DataResult<Unit>> result = new AtomicReference<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            stream.accept(t -> {
                final DataResult<Pair<A, T>> element = this.elementCodec.decode(ops, t);
                final DataResult<Pair<A, T>> res = element.flatMap(pair ->
                        read.contains(pair.getFirst())
                        ? DataResult.error("Duplicate entry has been added " + pair.getFirst(), pair, element.lifecycle())
                        : element
                );
                res.error().ifPresent(e -> failed.add(t));

                result.setPlain(result.getPlain().apply2stable((r, v) -> {
                    read.add(v.getFirst());
                    return r;
                }, res));
            });

            final ImmutableSet<A> elements = ImmutableSet.copyOf(read);
            final T errors = ops.createList(failed.build());
            final Pair<Set<A>, T> pair = Pair.of(elements, errors);

            return result.getPlain().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementCodec);
    }

    @Override
    public String toString() {
        return "SetCodec[" + elementCodec + ']';
    }
}
