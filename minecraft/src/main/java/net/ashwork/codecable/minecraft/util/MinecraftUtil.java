/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.minecraft.util;

import net.ashwork.codecable.util.GeneralUtil;
import net.ashwork.functionality.callable.CallableFunction;
import net.minecraft.util.StringRepresentable;

/**
 * A utility used to hold Minecraft specific generic
 * methods and fields that can be applied in multiple
 * places.
 */
public final class MinecraftUtil {

    /**
     * Constructs a function to get an enum from a string.
     * The function will return null if the string is null
     * or the string does not match one of the existing
     * enum names.
     *
     * @param enumClass The enum class
     * @param <E> The enum type
     * @return A {@link CallableFunction} that takes a string and returns an enum
     */
    public static <E extends Enum<E> & StringRepresentable> CallableFunction<? super String, ? extends E> stringToEnum(Class<E> enumClass) {
        return GeneralUtil.stringToEnum(enumClass, StringRepresentable::getSerializedName);
    }
}
