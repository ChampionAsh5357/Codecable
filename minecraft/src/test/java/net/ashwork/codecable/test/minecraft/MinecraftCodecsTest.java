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
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ashwork.codecable.minecraft.RecipeCodecs;
import net.ashwork.codecable.minecraft.codec.RecipeCodec;
import net.ashwork.codecable.minecraft.codec.SimpleRecipeCodec;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.MinecraftComparisonUtil;
import net.ashwork.codecable.test.util.MinecraftTestUtil;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * All tests associated with {@code net.ashwork.codecable.minecraft.codec}.
 */
public final class MinecraftCodecsTest {

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
     *     <li>{@link RecipeCodec#codec(ResourceLocation)}</li>
     * </ul>
     */
    @Test
    public void recipeCodec() {
        SimpleRecipeCodec<StonecutterRecipe> codec = new SimpleRecipeCodec<>(id ->
                RecordCodecBuilder.create(instance ->
                        instance.group(RecordCodecBuilder.point(id),
                                Codec.STRING.fieldOf("group").forGetter(StonecutterRecipe::getGroup),
                                RecipeCodecs.INGREDIENT.fieldOf("ingredient").forGetter(recipe -> recipe.getIngredients().get(0)),
                                ItemStack.CODEC.fieldOf("result").forGetter(StonecutterRecipe::getResultItem)
                        ).apply(instance, StonecutterRecipe::new)
                )
        );
        StonecutterRecipe testRecipe = new StonecutterRecipe(new ResourceLocation("test", "test_entry"),
                "test_group",
                Ingredient.of(GenerationUtil.selectRandom(Registry.ITEM.stream().toList())),
                new ItemStack(GenerationUtil.selectRandom(Registry.ITEM.stream().toList()),
                        GenerationUtil.generateInt(1, 65)
                ));
        //TODO: Abstract TestUtil#codecTest to support this use-case
        JsonObject element = new JsonObject();
        element.addProperty("group", testRecipe.getGroup());
        element.add("ingredient", testRecipe.getIngredients().get(0).toJson());
        element.add("result", ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, testRecipe.getResultItem()).result().orElseThrow());
        StonecutterRecipe deserialized = codec.fromJson(testRecipe.getId(), element);
        Assertions.assertTrue(MinecraftComparisonUtil.areRecipesEqual(testRecipe, deserialized));

        MinecraftTestUtil.networkTest(testRecipe, (buffer, recipe) -> {
            buffer.writeResourceLocation(recipe.getId());
            codec.toNetwork(buffer, recipe);
        }, buffer -> codec.fromNetwork(buffer.readResourceLocation(), buffer),
                MinecraftComparisonUtil::areRecipesEqual);
    }
}
