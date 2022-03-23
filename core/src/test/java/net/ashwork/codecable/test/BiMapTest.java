/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.test;

import com.google.common.collect.BiMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import net.ashwork.codecable.Codecable;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

/**
 * A test utility for bimap codecs.
 */
public final class BiMapTest {

    private final JsonObject clean, error, duplicate;
    private final Keyable keys;

    /**
     * Default constructor.
     */
    public BiMapTest() {
        this.clean = new JsonObject();
        this.clean.addProperty("test1", "test1");
        this.clean.addProperty("test2", "test2");
        this.clean.addProperty("test3", "test3");
        this.clean.addProperty("test4", "test4");

        this.error = new JsonObject();
        this.error.addProperty("test1", "test1");
        this.error.addProperty("test2", 4);
        this.error.addProperty("test3", "test3");
        this.error.addProperty("test4", "test4");

        this.duplicate = new JsonObject();
        this.duplicate.addProperty("test1", "test1");
        this.duplicate.addProperty("test2", "test1");
        this.duplicate.addProperty("test3", "test3");
        this.duplicate.addProperty("test4", "test4");

        this.keys = Keyable.forStrings(() -> Stream.of("test1", "test2", "test3", "test4"));
    }

    /**
     * Tests {@link Codecable#unboundedBiMap(Codec, Codec)}.
     */
    @Test
    public void unboundedMapNormal() {
        final Codec<BiMap<String, String>> codec = Codecable.unboundedBiMap(Codec.STRING, Codec.STRING);

        this.runNormalBiMapTest(codec);
    }

    /**
     * Tests {@link Codecable#unboundedBiMap(Codec, Codec, boolean)} when
     * duplicates should fail.
     */
    @Test
    public void unboundedMapDuplicateFail() {
        final Codec<BiMap<String, String>> codec = Codecable.unboundedBiMap(Codec.STRING, Codec.STRING, true);

        this.runFailDuplicateBiMapTest(codec);
    }

    /**
     * Tests {@link Codecable#unboundedBiMap(Codec, Codec, boolean, boolean)} when
     * duplicates should fail and bimaps should not be decoded passed the first
     * failed entry.
     */
    @Test
    public void unboundedMapStopOnFirst() {
        final Codec<BiMap<String, String>> codec = Codecable.unboundedBiMap(Codec.STRING, Codec.STRING, true, true);

        this.runStopOnErrorBiMapTest(codec);
    }

    /**
     * Tests {@link Codecable#simpleBiMap(Codec, Codec, Keyable)}.
     */
    @Test
    public void simpleMapNormal() {
        final Codec<BiMap<String, String>> codec = Codecable.simpleBiMap(Codec.STRING, Codec.STRING, this.keys).codec();

        this.runNormalBiMapTest(codec);
    }

    /**
     * Tests {@link Codecable#simpleBiMap(Codec, Codec, Keyable, boolean)} when
     * duplicates should fail.
     */
    @Test
    public void simpleMapDuplicateFail() {
        final Codec<BiMap<String, String>> codec = Codecable.simpleBiMap(Codec.STRING, Codec.STRING, this.keys, true).codec();

        this.runFailDuplicateBiMapTest(codec);
    }

    /**
     * Tests {@link Codecable#simpleBiMap(Codec, Codec, Keyable, boolean, boolean)}
     * when duplicates should fail and bimaps should not be decoded passed the first
     * failed entry.
     */
    @Test
    public void simpleMapStopOnFirst() {
        final Codec<BiMap<String, String>> codec = Codecable.simpleBiMap(Codec.STRING, Codec.STRING, this.keys, true, true).codec();

        this.runStopOnErrorBiMapTest(codec);
    }

    /**
     * Runs a normal bimap codec test.
     *
     * @param codec a bimap codec
     */
    private void runNormalBiMapTest(final Codec<BiMap<String, String>> codec) {
        SetTest.runNormalMapBasedTest(this.clean, this.error, this.duplicate, codec, BiMap::size);
    }

    /**
     * Runs a bimap codec test where bimaps should fail when a duplicate is
     * detected.
     *
     * @param codec a bimap codec
     */
    private void runFailDuplicateBiMapTest(final Codec<BiMap<String, String>> codec) {
        SetTest.runFailDuplicateMapBasedTest(this.clean, this.error, this.duplicate, codec, BiMap::size);
    }

    /**
     * Runs a bimap codec test where bimaps should not be decoded passed the first
     * failed entry.
     *
     * @param codec a bimap codec
     */
    private void runStopOnErrorBiMapTest(final Codec<BiMap<String, String>> codec) {
        SetTest.runStopOnErrorMapBasedTest(this.clean, this.error, this.duplicate, codec, BiMap::size);
    }
}
