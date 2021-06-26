/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * A utility for generating random inputs.
 */
public final class GenerationUtil {

    /**
     * A random instance.
     */
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * Generates a list of some size with random values.
     *
     * @param maxSize The maximum list size
     * @param collection The collection of values to choose from
     * @return The generated list
     * @throws IllegalArgumentException If the {@code maxSize} is a zero
     *                                  or negative.
     * @see #generateList(int, Collection)
     */
    public static <T> List<T> generateDynamicList(final int maxSize, final Collection<T> collection) {
        if (maxSize <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized list.");
        return generateList(generateInt(1, maxSize + 1), collection);
    }

    /**
     * Generates a list with random values.
     *
     * @param size The list size
     * @param collection The collection of values to choose from
     * @return The generated list
     * @throws IllegalArgumentException If the {@code size} is a zero
     *                                  or negative.
     * @see #selectRandom(Collection)
     */
    public static <T> List<T> generateList(final int size, final Collection<T> collection) {
        if (size <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized list.");
        List<T> res = new ArrayList<>(size);
        for (int i = 0; i < size; i++) res.add(selectRandom(collection));
        return res;
    }

    /**
     * Generates a set of some size with random integers.
     *
     * @param maxSize The maximum set size
     * @param min The minimum value within the set (inclusive)
     * @param max The maximum value within the set (exclusive)
     * @return The generated set
     * @throws IllegalArgumentException If the {@code maxSize} is a zero
     *                                  or negative.
     * @see #generateIntSet(int, int, int)
     */
    public static Set<Integer> generateDynamicIntSet(final int maxSize, final int min, final int max) {
        if (maxSize <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized set.");
        return generateIntSet(generateInt(1, maxSize + 1), min, max);
    }

    /**
     * Generates a set with random integers.
     *
     * @param size The set size
     * @param min The minimum value within the set (inclusive)
     * @param max The maximum value within the set (exclusive)
     * @return The generated set
     * @throws IllegalArgumentException If the {@code size} is a zero
     *                                  or negative.
     */
    public static Set<Integer> generateIntSet(final int size, final int min, final int max) {
        if (size <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized set.");
        return RANDOM.ints(size, min, max).distinct().boxed().collect(Collectors.toSet());
    }

    /**
     * Generates an array of some size with random integers.
     *
     * @param maxSize The maximum array size
     * @param min The minimum value within the array (inclusive)
     * @param max The maximum value within the array (exclusive)
     * @return The generated array
     * @throws IllegalArgumentException If the {@code maxSize} is a zero
     *                                  or negative, or if the difference
     *                                  between the maximum and minimum is
     *                                  zero or negative.
     * @see #generateIntArray(int, int, int)
     */
    public static int[] generateDynamicIntArray(final int maxSize, final int min, final int max) {
        if (maxSize <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized array.");
        return generateIntArray(generateInt(1, maxSize + 1), min, max);
    }

    /**
     * Generates an array with random integers.
     *
     * @param size The array size
     * @param min The minimum value within the array (inclusive)
     * @param max The maximum value within the array (exclusive)
     * @return The generated array
     * @throws IllegalArgumentException If the {@code maxSize} is a zero
     *                                  or negative, or if the difference
     *                                  between the maximum and minimum is
     *                                  zero or negative.
     */
    public static int[] generateIntArray(final int size, final int min, final int max) {
        if (size <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized array.");
        return RANDOM.ints(size, min, max).toArray();
    }

    /**
     * Generates an array with random longs.
     *
     * @param size The array size
     * @param min The minimum value within the array (inclusive)
     * @param max The maximum value within the array (exclusive)
     * @return The generated array
     * @throws IllegalArgumentException If the {@code maxSize} is a zero
     *                                  or negative, or if the difference
     *                                  between the maximum and minimum is
     *                                  zero or negative.
     */
    public static long[] generateLongArray(final long size, final long min, final long max) {
        if (size <= 0) throw new IllegalArgumentException("Cannot have a negative or zero sized array.");
        return RANDOM.longs(size, min, max).toArray();
    }

    /**
     * Generates a random integer between the bounds.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (exclusive)
     * @return The generated integer
     */
    public static int generateInt(final int min, final int max) {
        return RANDOM.nextInt(min, max);
    }

    /**
     * Generates a random float between the bounds.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (exclusive)
     * @return The generated float
     */
    public static float generateFloat(final float min, final float max) {
        return (max - min) * RANDOM.nextFloat() + min;
    }

    /**
     * Generates a random double between the bounds.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (exclusive)
     * @return The generated double
     */
    public static double generateDouble(final double min, final double max) {
        return RANDOM.nextDouble(min, max);
    }

    /**
     * Selects a random value from a collection.
     *
     * @param collection The collection selected from
     * @param <T> The value type
     * @return The random value
     */
    public static <T> T selectRandom(final Collection<T> collection) {
        return collection.stream().skip(RANDOM.nextInt(collection.size())).findFirst().orElseThrow();
    }

    /**
     * Selects a random value from a {@link JsonArray}.
     *
     * @param arr The array selected from
     * @return The random element
     */
    public static JsonElement selectRandom(final JsonArray arr) {
        return arr.get(RANDOM.nextInt(arr.size()));
    }

    /**
     * Selects a random value from an array.
     *
     * @param arr The array selected from
     * @param <T> The value type
     * @return The random value
     */
    public static <T> T selectRandom(final T[] arr) {
        return arr[RANDOM.nextInt(arr.length)];
    }
}
