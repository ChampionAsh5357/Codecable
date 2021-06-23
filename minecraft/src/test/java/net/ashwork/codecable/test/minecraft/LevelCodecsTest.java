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
import com.google.gson.JsonPrimitive;
import net.ashwork.codecable.minecraft.LevelCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * All tests associated with {@link LevelCodecs}.
 */
public final class LevelCodecsTest {

    /**
     * Initial registry setup to run tests.
     */
    @BeforeAll
    public static void setup() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link LevelCodecs#CHUNK_POS}</li>
     *     <li>{@link LevelCodecs#PRETTY_CHUNK_POS}</li>
     *     <li>{@link LevelCodecs#COMPRESSED_CHUNK_POS}</li>
     * </ul>
     */
    @Test
    public void chunkPos() {
        ChunkPos pos = new ChunkPos(GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000));

        JsonArray posOutput = new JsonArray();
        posOutput.add(pos.x);
        posOutput.add(pos.z);
        TestUtil.codecJsonTest(LevelCodecs.CHUNK_POS, pos, posOutput);

        JsonObject prettyPosOutput = new JsonObject();
        prettyPosOutput.addProperty("x", pos.x);
        prettyPosOutput.addProperty("z", pos.z);
        TestUtil.codecJsonTest(LevelCodecs.PRETTY_CHUNK_POS, pos, prettyPosOutput);

        JsonPrimitive compressedPosOutput = new JsonPrimitive(pos.toLong());
        TestUtil.codecJsonTest(LevelCodecs.COMPRESSED_CHUNK_POS, pos, compressedPosOutput);
    }

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link LevelCodecs#BLOCK_EVENT_DATA}</li>
     * </ul>
     */
    @Test
    public void blockEventData() {
        BlockEventData data = new BlockEventData(new BlockPos(GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000)),
                GenerationUtil.selectRandom(Registry.BLOCK.stream().toList()), GenerationUtil.generateInt(0, 1000), GenerationUtil.generateInt(0, 1000));

        JsonObject dataOutput = new JsonObject();
        JsonArray pos = new JsonArray();
        pos.add(data.getPos().getX());
        pos.add(data.getPos().getY());
        pos.add(data.getPos().getZ());
        dataOutput.add("pos", pos);
        dataOutput.addProperty("block", Registry.BLOCK.getKey(data.getBlock()).toString());
        dataOutput.addProperty("paramA", data.getParamA());
        dataOutput.addProperty("paramB", data.getParamB());
        TestUtil.codecJsonTest(LevelCodecs.BLOCK_EVENT_DATA, data, dataOutput);
    }
}
