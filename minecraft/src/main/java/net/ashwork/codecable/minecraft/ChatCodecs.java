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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

/**
 * Codecs for classes within {@code net.minecraft.network.chat}.
 */
public final class ChatCodecs {

    /**
     * A {@link MutableComponent} codec. This will catch any exceptions and return a stack trace associated with them.
     */
    public static final Codec<MutableComponent> MUTABLE_COMPONENT =
            ErrorUtil.flatXmapHandleUncaughtExceptions(MutableComponent.class, Codec.PASSTHROUGH,
                    dynamic -> {
                        @Nullable final MutableComponent component = Component.Serializer.fromJson(
                                dynamic.convert(JsonOps.INSTANCE).getValue()
                        );
                        return component != null ? DataResult.success(component)
                                : DataResult.error("Not a valid mutable component, returned null.");
                        },
                    component -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, Component.Serializer.toJsonTree(component)))
            );
}
