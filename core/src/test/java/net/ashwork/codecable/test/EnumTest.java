/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.Codecable;
import net.ashwork.codecable.function.ThrowingFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A test utility for enum codecs
 */
public final class EnumTest {

    private TestEnum test;
    private final JsonPrimitive i, s;

    /**
     * Default constructor.
     */
    public EnumTest() {
        this.i = new JsonPrimitive(TestEnum.values().length);
        this.s = new JsonPrimitive("nil");
    }

    /**
     * Picks a random enum value to use to test.
     */
    @BeforeEach
    public void beforeEach() {
        this.test = TestEnum.values()[ThreadLocalRandom.current().nextInt(TestEnum.values().length)];
    }

    /**
     * Tests {@link Codecable#enumOf(Class)}.
     */
    @Test
    public void regularEnum() {
        final Codec<TestEnum> codec = Codecable.enumOf(TestEnum.class);

        this.runEnumTest(codec);
    }

    /**
     * Tests {@link Codecable#enumOf(Class, ThrowingFunction, Function)}.
     */
    @Test
    public void strEnum() {
        final Codec<TestEnum> codec = Codecable.enumOf(TestEnum.class, TestEnum::byNickname, TestEnum::nickname);

        this.runEnumTest(codec);
    }

    /**
     * Tests {@link Codecable#enumOf(ThrowingFunction, Function, ThrowingFunction, ToIntFunction)}.
     */
    @Test
    public void strIdEnum() {
        final Codec<TestEnum> codec = Codecable.enumOf(TestEnum::byNickname, TestEnum::nickname, TestEnum::byId, TestEnum::id);

        this.runEnumTest(codec);
    }

    /**
     * Runs an enum test on the randomly selected enum and error instances.
     *
     * @param codec the enum codec
     */
    private void runEnumTest(final Codec<TestEnum> codec) {
        runEnumTest(this.test, codec, this.s, this.i);
    }

    /**
     * Creates a test for an enum. Verifies it can encode and decode its data.
     *
     * @param instance an enum instance
     * @param codec the enum codec
     * @param errorString a json primitive of an independent string
     * @param errorInt a json primitive of an independent {@code int}
     * @param <E> the type of the enum
     */
    private static <E extends Enum<E>> void runEnumTest(final E instance, final Codec<E> codec, final JsonElement errorString, final JsonElement errorInt) {
        Assertions.assertDoesNotThrow(() -> {
            final JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, System.out::println);
            Assertions.assertEquals(instance, codec.parse(JsonOps.INSTANCE, encoded).getOrThrow(false, System.out::println));
        });

        Assertions.assertDoesNotThrow(() -> {
            final JsonElement encoded = codec.encodeStart(JsonOps.COMPRESSED, instance).getOrThrow(false, System.out::println);
            Assertions.assertEquals(instance, codec.parse(JsonOps.COMPRESSED, encoded).getOrThrow(false, System.out::println));
        });

        Assertions.assertThrows(RuntimeException.class, () -> codec.parse(JsonOps.INSTANCE, errorString).getOrThrow(false, System.out::println));
        Assertions.assertThrows(RuntimeException.class, () -> codec.parse(JsonOps.COMPRESSED, errorInt).getOrThrow(false, System.out::println));
    }

    /**
     * A enum for testing.
     */
    private enum TestEnum {
        TEST1(5, "t1"),
        TEST2(142, "t2"),
        TEST3(92, "t3"),
        TEST4(1, "t4");

        private final int id;
        private final String nickname;

        /**
         * Default constructor.
         *
         * @param id a non-ordinal {@code int}
         * @param nickname a non-name string
         */
        TestEnum(final int id, final String nickname) {
            this.id = id;
            this.nickname = nickname;
        }

        /**
         * Returns a non-ordinal {@code int}.
         *
         * @return a non-ordinal {@code int}
         */
        public int id() {
            return this.id;
        }

        /**
         * Returns a non-name string.
         *
         * @return a non-name string
         */
        public String nickname() {
            return this.nickname;
        }

        /**
         * Finds the enum by its non-name string or throws an exception.
         *
         * @param nickname a non-name string
         * @return an enum
         * @throws NoSuchElementException if no enum has the non-name string
         */
        public static TestEnum byNickname(final String nickname) {
            return Arrays.stream(TestEnum.values()).filter(e -> nickname.equalsIgnoreCase(e.nickname())).findFirst().orElseThrow();
        }

        /**
         * Finds the enum by its non-ordinal {@code int} or throws an exception.
         *
         * @param id a non-ordinal {@code int}
         * @return an enum
         * @throws NoSuchElementException if no enum has the non-ordinal {@code int}
         */
        public static TestEnum byId(final int id) {
            return Arrays.stream(TestEnum.values()).filter(e -> id == e.id()).findFirst().orElseThrow();
        }
    }
}
