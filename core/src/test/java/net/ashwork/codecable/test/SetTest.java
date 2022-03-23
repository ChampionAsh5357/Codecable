/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.Codecable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * A test utility for set codecs.
 */
public final class SetTest {

    private final JsonArray clean, error, duplicate;

    /**
     * Default constructor.
     */
    public SetTest() {
        this.clean = new JsonArray();
        this.clean.add("test1");
        this.clean.add("test2");
        this.clean.add("test3");
        this.clean.add("test4");

        this.error = new JsonArray();
        this.error.add("test1");
        this.error.add(4);
        this.error.add("test3");
        this.error.add("test4");

        this.duplicate = new JsonArray();
        this.duplicate.add("test1");
        this.duplicate.add("test1");
        this.duplicate.add("test3");
        this.duplicate.add("test4");
    }

    /**
     * Tests {@link Codecable#set(Codec)}.
     */
    @Test
    public void setNormal() {
        final Codec<Set<String>> codec = Codecable.wrap(Codec.STRING).setOf();

        this.runNormalSetTest(codec);
    }

    /**
     * Tests {@link Codecable#set(Codec, boolean)} when duplicates should fail.
     */
    @Test
    public void setDuplicateFail() {
        final Codec<Set<String>> codec = Codecable.wrap(Codec.STRING).setOf(true);

        this.runFailDuplicateSetTest(codec);
    }

    /**
     * Tests {@link Codecable#set(Codec, boolean, boolean)} when duplicates should
     * fail and sets should not be decoded passed the first failed element.
     */
    @Test
    public void setStopOnFirst() {
        final Codec<Set<String>> codec = Codecable.wrap(Codec.STRING).setOf(true, true);

        this.runStopOnErrorSetTest(codec);
    }

    /**
     * Runs a normal set codec test.
     *
     * @param codec a set codec
     */
    private void runNormalSetTest(final Codec<Set<String>> codec) {
        runNormalMapBasedTest(this.clean, this.error, this.duplicate, codec, Set::size);
    }

    /**
     * Runs a set codec test where sets should fail when a duplicate is detected.
     *
     * @param codec a set codec
     */
    private void runFailDuplicateSetTest(final Codec<Set<String>> codec) {
        runFailDuplicateMapBasedTest(this.clean, this.error, this.duplicate, codec, Set::size);
    }

    /**
     * Runs a set codec test where sets should not be decoded passed the first
     * failed entry.
     *
     * @param codec a set codec
     */
    private void runStopOnErrorSetTest(final Codec<Set<String>> codec) {
        runStopOnErrorMapBasedTest(this.clean, this.error, this.duplicate, codec, Set::size);
    }

    /**
     * Runs a normal map based codec test.
     *
     * @apiNote
     * If {@code duplicateInstance} is {@code null}, its test will not be run.
     *
     * @param normalInstance a normal encoded instance
     * @param errorInstance an encoded instance that will fail on decoding
     * @param duplicateInstance an encoded instance with a duplicate entry
     * @param codec a map based codec
     * @param sizeFunction a function to get the length of the map
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    public static <A, T extends JsonElement> void runNormalMapBasedTest(final T normalInstance, final T errorInstance, final @Nullable T duplicateInstance, final Codec<A> codec, final ToIntFunction<A> sizeFunction) {
        runMapBasedTest(normalInstance, errorInstance, duplicateInstance, SetTest::runNormalTest, codec, sizeFunction, 4, 3, 3);
    }

    /**
     * Runs a map based codec test where maps should fail when a duplicate is
     * detected.
     *
     * @param normalInstance a normal encoded instance
     * @param errorInstance an encoded instance that will fail on decoding
     * @param duplicateInstance an encoded instance with a duplicate entry
     * @param codec a map based codec
     * @param sizeFunction a function to get the length of the map
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    public static <A, T extends JsonElement> void runFailDuplicateMapBasedTest(final T normalInstance, final T errorInstance, final T duplicateInstance, final Codec<A> codec, final ToIntFunction<A> sizeFunction) {
        runMapBasedTest(normalInstance, errorInstance, duplicateInstance, SetTest::runErrorTest, codec, sizeFunction, 4, 3, 3);
    }

    /**
     * Runs a map based codec test where maps should not be decoded passed the first
     * failed entry.
     *
     * @apiNote
     * If {@code duplicateInstance} is {@code null}, its test will not be run.
     *
     * @param normalInstance a normal encoded instance
     * @param errorInstance an encoded instance that will fail on decoding
     * @param duplicateInstance an encoded instance with a duplicate entry
     * @param codec a map based codec
     * @param sizeFunction a function to get the length of the map
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    public static <A, T extends JsonElement> void runStopOnErrorMapBasedTest(final T normalInstance, final T errorInstance, final @Nullable T duplicateInstance, final Codec<A> codec, final ToIntFunction<A> sizeFunction) {
        runMapBasedTest(normalInstance, errorInstance, duplicateInstance, SetTest::runErrorTest, codec, sizeFunction, 4, 1, 1);
    }

    /**
     * Runs a map based codec test.
     *
     * @param normalInstance a normal encoded instance
     * @param errorInstance an encoded instance that will fail on decoding
     * @param duplicateInstance an encoded instance with a duplicate entry
     * @param duplicateTest a test runner for the duplicate instance
     * @param codec a map based codec
     * @param sizeFunction a function to get the length of the map
     * @param normalExpectedSize the expected size of the normal map
     * @param errorExpectedSize the expected size of the errored map
     * @param duplicateExpectedSize the expected size of the duplicate map
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    public static <A, T extends JsonElement> void runMapBasedTest(final T normalInstance, final T errorInstance, final @Nullable T duplicateInstance,
                                                                  final MapBasedTestRunner<A, T> duplicateTest, final Codec<A> codec,
                                                                  final ToIntFunction<A> sizeFunction, final int normalExpectedSize, final int errorExpectedSize, final int duplicateExpectedSize) {
        runNormalTest(normalInstance, codec, sizeFunction, normalExpectedSize);

        runErrorTest(errorInstance, codec, sizeFunction, errorExpectedSize);

        if (duplicateInstance != null) duplicateTest.run(duplicateInstance, codec, sizeFunction, duplicateExpectedSize);
    }

    /**
     * Runs a normal map based codec test.
     *
     * @param normalInstance a normal encoded instance
     * @param codec a map based codec
     * @param sizeFunction a function to get the length of the map
     * @param expectedSize the expected size of the map
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    private static <A, T extends JsonElement> void runNormalTest(final T normalInstance, final Codec<A> codec, final ToIntFunction<A> sizeFunction, final int expectedSize) {
        Assertions.assertDoesNotThrow(() -> {{
            final A res = codec.parse(JsonOps.INSTANCE, normalInstance).getOrThrow(false, System.out::println);
            Assertions.assertEquals(expectedSize, sizeFunction.applyAsInt(res));
        }});
    }

    /**
     * Runs an error map based codec test.
     *
     * @param errorInstance an encoded instance that will fail on decoding
     * @param codec a map based codec
     * @param sizeFunction a function to get the length of the map
     * @param expectedSize the expected size of the map
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    private static <A, T extends JsonElement> void runErrorTest(final T errorInstance, final Codec<A> codec, final ToIntFunction<A> sizeFunction, final int expectedSize) {
        Assertions.assertThrows(RuntimeException.class, () -> codec.parse(JsonOps.INSTANCE, errorInstance).getOrThrow(false, System.out::println));

        Assertions.assertDoesNotThrow(() -> {
            final A res = codec.parse(JsonOps.INSTANCE, errorInstance).getOrThrow(true, System.out::println);
            Assertions.assertEquals(expectedSize, sizeFunction.applyAsInt(res));
        });
    }

    /**
     * An interface used to run map based tests.
     *
     * @param <A> the type of the map-based object
     * @param <T> the type of the encoded format
     */
    @FunctionalInterface
    interface MapBasedTestRunner<A, T extends JsonElement> {

        /**
         * Runs a test.
         *
         * @param instance an encoded instance
         * @param codec a map based codec
         * @param sizeFunction a function to get the length of the map
         * @param expectedSize the expected size of the map
         */
        void run(final T instance, final Codec<A> codec, final ToIntFunction<A> sizeFunction, final int expectedSize);
    }
}
