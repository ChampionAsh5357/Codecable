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
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.util.ErrorUtil;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Codecs for classes within {@code net.minecraft.world.item.crafting}.
 */
public final class RecipeCodecs {

    /**
     * An {@link Ingredient} codec. This will catch any exceptions and return a stack trace associated with them.
     */
    public static final Codec<Ingredient> INGREDIENT =
            ErrorUtil.flatXmapHandleUncaughtExceptions(Ingredient.class, Codec.PASSTHROUGH,
                    dynamic -> DataResult.success(Ingredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue())),
                    ingredient -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()))
            );
}
