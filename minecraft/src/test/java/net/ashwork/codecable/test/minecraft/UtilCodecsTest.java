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
import net.ashwork.codecable.minecraft.UtilCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.MinecraftComparisonUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.util.BitStorage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * All tests associated with {@link UtilCodecs}.
 */
public final class UtilCodecsTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link UtilCodecs#BIT_STORAGE}</li>
     * </ul>
     */
    @Test
    public void bitStorage() {
        int size = GenerationUtil.generateInt(1, 8192) * 2 + 1, bits = 32;
        BitStorage storage = new BitStorage(bits, size, GenerationUtil.generateLongArray((size + (char)(64 / bits) + 1) / (char)(64 / bits) - 1, 0, 10000));
        JsonObject storageObject = new JsonObject();
        storageObject.addProperty("bits", bits);
        storageObject.addProperty("size", size);
        JsonArray data = new JsonArray();
        Arrays.stream(storage.getRaw()).forEach(data::add);
        storageObject.add("data", data);
        TestUtil.codecJsonTest(UtilCodecs.BIT_STORAGE, storage, storageObject, MinecraftComparisonUtil::areBitStoragesEqual);
    }
}
