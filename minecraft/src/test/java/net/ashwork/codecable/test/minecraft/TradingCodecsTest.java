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
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.minecraft.TradingCodecs;
import net.ashwork.codecable.test.util.GenerationUtil;
import net.ashwork.codecable.test.util.TestUtil;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * All tests associated with {@link TradingCodecs}.
 */
public class TradingCodecsTest {

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
     *     <li>{@link TradingCodecs#MERCHANT_OFFER}</li>
     * </ul>
     */
    @Test
    public void merchantOffer() {
        List<Item> items = GenerationUtil.generateList(3, Registry.ITEM.stream().toList());
        int[] sizes = GenerationUtil.generateIntArray(3, 1, 65);
        ItemStack[] stacks = IntStream.range(0, 3).mapToObj(i -> new ItemStack(items.get(i), sizes[i])).toArray(ItemStack[]::new);
        String[] names = new String[]{"buy", "buyB", "sell"};
        MerchantOffer offer = new MerchantOffer(stacks[0], stacks[1], stacks[2], GenerationUtil.generateInt(1, 100), GenerationUtil.generateInt(1, 100), GenerationUtil.generateInt(1, 1000), GenerationUtil.generateFloat(0.1F, 2.0F), GenerationUtil.generateInt(1, 100));
        JsonObject element = new JsonObject();
        for (int i = 0; i < stacks.length; i++)
            element.add(names[i], ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stacks[i]).result().orElseThrow());
        element.addProperty("uses", offer.getUses());
        element.addProperty("maxUses", offer.getMaxUses());
        element.addProperty("rewardExp", offer.shouldRewardExp() ? 1 : 0);
        element.addProperty("xp", offer.getXp());
        element.addProperty("priceMultiplier", offer.getPriceMultiplier());
        element.addProperty("specialPrice", offer.getSpecialPriceDiff());
        element.addProperty("demand", offer.getDemand());
        TestUtil.codecJsonTest(TradingCodecs.MERCHANT_OFFER, offer, element, TradingCodecsTest::areOffersEqual);
    }

    /**
     * Checks whether two offers are equivalent.
     *
     * @param original The first offer
     * @param deserialized The second offer
     * @return Whether the two are equal
     */
    private static boolean areOffersEqual(MerchantOffer original, MerchantOffer deserialized) {
        if (Objects.equals(original, deserialized)) return true;
        if (original == null || deserialized == null) return false;
        return ItemStack.matches(original.getBaseCostA(), deserialized.getBaseCostA())
                && ItemStack.matches(original.getCostB(), deserialized.getCostB())
                && ItemStack.matches(original.getResult(), deserialized.getResult())
                && original.getUses() == deserialized.getUses()
                && original.getMaxUses() == deserialized.getMaxUses()
                && original.shouldRewardExp() == deserialized.shouldRewardExp()
                && original.getSpecialPriceDiff() == deserialized.getSpecialPriceDiff()
                && original.getDemand() == deserialized.getDemand()
                && original.getPriceMultiplier() == deserialized.getPriceMultiplier()
                && original.getXp() == deserialized.getXp();
    }
}
