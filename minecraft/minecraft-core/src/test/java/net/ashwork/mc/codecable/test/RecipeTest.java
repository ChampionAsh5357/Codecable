/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable.test;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ashwork.mc.codecable.MinecraftCodecable;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A test utility for recipe codecs.
 */
public final class RecipeTest {

    /**
     * Loads the Minecraft bootstrap before running unit tests.
     */
    @BeforeAll
    public static void beforeAll() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    /**
     * Tests {@link MinecraftCodecable#INGREDIENT}.
     */
    @Test
    public void ingredient() {
        final Ingredient ingredient = Ingredient.of(ThreadLocalRandom.current().ints(10, 0, Registry.ITEM.size())
                .mapToObj(Registry.ITEM::byId).map(ItemStack::new));
        final ItemStack[] stacks = ingredient.getItems();
        final Codec<Ingredient> codec = MinecraftCodecable.INGREDIENT;

        Assertions.assertDoesNotThrow(() -> {
            final JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow(false, System.out::println);
            final ItemStack[] r = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow(false, System.out::println).getItems();

            Assertions.assertEquals(stacks.length, r.length);
            for (int i = 0; i < stacks.length; ++i)
                Assertions.assertTrue(ItemStack.isSameItemSameTags(stacks[i], r[i]));
        });
    }
}
