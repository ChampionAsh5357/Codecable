/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * Codecs for classes within {@code net.minecraft.world.item.trading}.
 */
public final class TradingCodecs {

    /**
     * A {@link MerchantOffer} codec. Converts the data from the nbt serializers present.
     */
    public static final Codec<MerchantOffer> MERCHANT_OFFER = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        return tag instanceof CompoundTag compoundTag ? Util.make(() -> {
            compoundTag.putByte("rewardExp", compoundTag.getByte("rewardExp"));
            compoundTag.putInt("xp", compoundTag.getInt("xp"));
            compoundTag.putFloat("priceMultiplier", compoundTag.getFloat("priceMultiplier"));
            return DataResult.success(new MerchantOffer(compoundTag));
        })
                : DataResult.error("Not a compound tag to deserialize MerchantOffer: " + tag);
    }, offer -> new Dynamic<>(NbtOps.INSTANCE, offer.createTag()));
}
