/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.test;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import net.ashwork.codecable.Codecable;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

/**
 * A test utility for map codecs.
 */
public final class ImprovedMapTest {

    private final JsonObject clean, error;
    private final Keyable keys;

    /**
     * Default constructor.
     */
    public ImprovedMapTest() {
        this.clean = new JsonObject();
        this.clean.addProperty("test1", "test1");
        this.clean.addProperty("test2", "test2");
        this.clean.addProperty("test3", "test3");
        this.clean.addProperty("test4", "test1");

        this.error = new JsonObject();
        this.error.addProperty("test1", "test1");
        this.error.addProperty("test2", 4);
        this.error.addProperty("test3", "test3");
        this.error.addProperty("test4", "test4");

        this.keys = Keyable.forStrings(() -> Stream.of("test1", "test2", "test3", "test4"));
    }

    /**
     * Tests {@link Codecable#improvedUnboundedMap(Codec, Codec)}.
     */
    @Test
    public void unboundedMapNormal() {
        final Codec<Map<String, String>> codec = Codecable.improvedUnboundedMap(Codec.STRING, Codec.STRING);

        this.runNormalMapTest(codec);
    }

    /**
     * Tests {@link Codecable#improvedUnboundedMap(Codec, Codec, boolean)} when maps
     * should not be decoded passed the first failed entry.
     */
    @Test
    public void unboundedStopOnFirst() {
        final Codec<Map<String, String>> codec = Codecable.improvedUnboundedMap(Codec.STRING, Codec.STRING, true);

        this.runStopOnErrorMapTest(codec);
    }

    /**
     * Tests {@link Codecable#improvedSimpleMap(Codec, Codec, Keyable)}.
     */
    @Test
    public void simpleMapNormal() {
        final Codec<Map<String, String>> codec = Codecable.improvedSimpleMap(Codec.STRING, Codec.STRING, this.keys).codec();

        this.runNormalMapTest(codec);
    }

    /**
     * Tests {@link Codecable#improvedSimpleMap(Codec, Codec, Keyable, boolean)}
     * when maps should not be decoded passed the first failed entry.
     */
    @Test
    public void simpleStopOnFirst() {
        final Codec<Map<String, String>> codec = Codecable.improvedSimpleMap(Codec.STRING, Codec.STRING, this.keys, true).codec();

        this.runStopOnErrorMapTest(codec);
    }

    /**
     * Runs a normal map codec test.
     *
     * @param codec a map codec
     */
    private void runNormalMapTest(final Codec<Map<String, String>> codec) {
        SetTest.runNormalMapBasedTest(this.clean, this.error, null, codec, Map::size);
    }

    /**
     * Runs a map codec test where maps should not be decoded passed the first
     * failed entry.
     *
     * @param codec a map codec
     */
    private void runStopOnErrorMapTest(final Codec<Map<String, String>> codec) {
        SetTest.runStopOnErrorMapBasedTest(this.clean, this.error, null, codec, Map::size);
    }
}
