/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.util;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

/**
 * A utility for comparing two objects.
 */
public final class ComparisonUtil {

    /**
     * Checks whether two lists are equivalent.
     *
     * @param thisObject The first list
     * @param thatObject The second list
     * @param equalsTest An equality test for the objects within the list
     * @param <T> The type of the elements within the list
     * @return If the two lists are equal
     */
    public static <T> boolean areListsEqual(List<T> thisObject, List<T> thatObject, BiPredicate<T, T> equalsTest) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return thisObject.size() == thatObject.size()
                && IntStream.range(0, thisObject.size()).allMatch(i ->
                equalsTest.test(thisObject.get(i), thatObject.get(i)
                )
        );
    }
}
