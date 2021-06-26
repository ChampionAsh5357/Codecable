/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.test.util;

import net.minecraft.util.BitStorage;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.phys.Vec2;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

/**
 * A utility for comparing two objects within Minecraft.
 */
public final class MinecraftComparisonUtil {

    /**
     * Checks whether two ingredients are equivalent.
     *
     * @param thisObject The first ingredient
     * @param thatObject The second ingredient
     * @return Whether the two are equal
     */
    public static boolean areIngredientsEqual(final Ingredient thisObject, final Ingredient thatObject) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        List<ItemStack> deserializedStacks = Arrays.asList(thatObject.getItems());
        return Arrays.stream(thisObject.getItems()).allMatch(thisStack ->
                deserializedStacks.stream().anyMatch(thatStack ->
                        ItemStack.matches(thisStack, thatStack)
                )
        );
    }

    /**
     * Checks whether two recipes are equivalent.
     *
     * @param thisObject The first offer
     * @param thatObject The second offer
     * @return Whether the two are equal
     */
    public static boolean areRecipesEqual(final Recipe<?> thisObject, final Recipe<?> thatObject) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return thisObject.getClass() == thatObject.getClass()
                && Objects.equals(thisObject.getId(), thatObject.getId())
                && Objects.equals(thisObject.getGroup(), thatObject.getGroup())
                && ComparisonUtil.areListsEqual(thisObject.getIngredients(), thatObject.getIngredients(), MinecraftComparisonUtil::areIngredientsEqual)
                && ItemStack.matches(thisObject.getResultItem(), thatObject.getResultItem());
    }

    /**
     * Checks whether two Vec2s are equivalent.
     *
     * @param thisObject The first vec2
     * @param thatObject The second vec2
     * @return Whether the two are equal
     */
    public static boolean areVec2sEqual(final Vec2 thisObject, final Vec2 thatObject) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return thisObject.x == thatObject.x && thisObject.y == thatObject.y;
    }

    /**
     * Checks whether two bit storages are equivalent.
     *
     * @param thisObject The first bit storage
     * @param thatObject The second bit storage
     * @return Whether the two are equal
     */
    public static boolean areBitStoragesEqual(final BitStorage thisObject, final BitStorage thatObject) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return thisObject.getBits() == thatObject.getBits() && thisObject.getSize() == thatObject.getSize()
                && IntStream.range(0, thisObject.getRaw().length).allMatch(i -> thisObject.getRaw()[i] == thatObject.getRaw()[i]);
    }

    /**
     * Checks whether two tuples are equivalent.
     *
     * @param thisObject The first tuple
     * @param thatObject The second tuple
     * @param aEqualsTest An equality test for the first element within the tuple
     * @param bEqualsTest An equality test for the second element within the tuple
     * @param <A> The type of the first element in the tuple
     * @param <B> The type of the second element in the tuple
     * @return Whether the two are equal
     */
    public static <A, B> boolean areTuplesEqual(final Tuple<A, B> thisObject, final Tuple<A, B> thatObject, final BiPredicate<A, A> aEqualsTest, final BiPredicate<B, B> bEqualsTest) {
        if (Objects.equals(thisObject, thatObject)) return true;
        if (thisObject == null || thatObject == null) return false;
        return aEqualsTest.test(thisObject.getA(), thatObject.getA()) && bEqualsTest.test(thisObject.getB(), thatObject.getB());
    }
}
