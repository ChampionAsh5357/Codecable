/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.ashwork.codecable.minecraft.LevelGenCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.Test;

/**
 * All tests associated with {@link LevelGenCodecs}.
 */
public final class LevelGenCodecsTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link LevelGenCodecs#BOUNDING_BOX}</li>
     *     <li>{@link LevelGenCodecs#PRETTY_BOUNDING_BOX}</li>
     * </ul>
     */
    @Test
    public void boundingBox() {
        BoundingBox box = new BoundingBox(GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000));

        JsonObject boxOutput = new JsonObject();
        JsonArray boxMin = new JsonArray();
        boxMin.add(box.minX());
        boxMin.add(box.minY());
        boxMin.add(box.minZ());
        boxOutput.add("min", boxMin);
        JsonArray boxMax = new JsonArray();
        boxMax.add(box.maxX());
        boxMax.add(box.maxY());
        boxMax.add(box.maxZ());
        boxOutput.add("max", boxMax);
        TestUtil.codecJsonTest(LevelGenCodecs.BOUNDING_BOX, box, boxOutput);

        JsonObject prettyBoxOutput = new JsonObject();
        prettyBoxOutput.addProperty("minX", box.minX());
        prettyBoxOutput.addProperty("minY", box.minY());
        prettyBoxOutput.addProperty("minZ", box.minZ());
        prettyBoxOutput.addProperty("maxX", box.maxX());
        prettyBoxOutput.addProperty("maxY", box.maxY());
        prettyBoxOutput.addProperty("maxZ", box.maxZ());
        TestUtil.codecJsonTest(LevelGenCodecs.PRETTY_BOUNDING_BOX, box, prettyBoxOutput);
    }
}
