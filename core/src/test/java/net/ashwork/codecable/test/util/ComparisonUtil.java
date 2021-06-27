/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.util;

import com.google.common.collect.Lists;

import java.util.*;
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

    /**
     * Checks whether two iterators are equivalent.
     *
     * @param thisObject The first iterator
     * @param thatObject The second iterator
     * @param equalsTest An equality test for the objects within the iterator
     * @param <T> The type of the elements within the iterator
     * @return If the two iterators are equal
     */
    public static <T> boolean areIteratorsEqual(Iterator<T> thisObject, Iterator<T> thatObject, BiPredicate<T, T> equalsTest) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return areListsEqual(Lists.newArrayList(thisObject), Lists.newArrayList(thatObject), equalsTest);
    }

    /**
     * Checks whether two enumerations are equivalent.
     *
     * @param thisObject The first enumeration
     * @param thatObject The second enumeration
     * @param equalsTest An equality test for the objects within the enumeration
     * @param <T> The type of the elements within the enumeration
     * @return If the two enumerations are equal
     */
    public static <T> boolean areEnumerationsEqual(Enumeration<T> thisObject, Enumeration<T> thatObject, BiPredicate<T, T> equalsTest) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return areListsEqual(Collections.list(thisObject), Collections.list(thatObject), equalsTest);
    }
}
