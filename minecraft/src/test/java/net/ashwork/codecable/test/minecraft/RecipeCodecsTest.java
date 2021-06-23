/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.minecraft;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.ashwork.codecable.minecraft.RecipeCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.MinecraftComparisonUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * All tests associated with {@link RecipeCodecs}.
 */
public final class RecipeCodecsTest {

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
     *     <li>{@link RecipeCodecs#INGREDIENT}</li>
     * </ul>
     */
    @Test
    public void ingredient() {
        List<Item> items = GenerationUtil.generateDynamicList(10, Registry.ITEM.stream().toList());
        Ingredient item = Ingredient.of(Iterables.toArray(items, Item.class));
        JsonArray element = new JsonArray();
        Arrays.stream(item.getItems()).forEach(stack -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
            element.add(obj);
        });
        TestUtil.codecJsonTest(RecipeCodecs.INGREDIENT, item, element, MinecraftComparisonUtil::areIngredientsEqual);
    }
}
