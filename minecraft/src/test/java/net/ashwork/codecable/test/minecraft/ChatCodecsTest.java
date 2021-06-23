/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.minecraft;

import com.google.gson.JsonObject;
import net.ashwork.codecable.minecraft.ChatCodecs;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.junit.jupiter.api.Test;

/**
 * All tests associated with {@link ChatCodecs}.
 */
public final class ChatCodecsTest {

    /**
     * Codec(s) Tested:
     * <ul>
     *     <li>{@link ChatCodecs#MUTABLE_COMPONENT}</li>
     * </ul>
     */
    @Test
    public void mutableComponent() {
        // Text component
        String textStr = "Test text 1";
        TextComponent text = new TextComponent(textStr);
        JsonObject textElement = new JsonObject();
        textElement.addProperty("text", textStr);
        TestUtil.codecJsonTest(ChatCodecs.MUTABLE_COMPONENT, text, textElement);

        // Translatable component
        String translatableStr = "test.text.desc";
        TranslatableComponent translatable = new TranslatableComponent(translatableStr);
        JsonObject translatableElement = new JsonObject();
        translatableElement.addProperty("translate", translatableStr);
        TestUtil.codecJsonTest(ChatCodecs.MUTABLE_COMPONENT, translatable, translatableElement);
    }
}
