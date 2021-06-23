/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.util;

import net.ashwork.functionality.callable.CallableFunction;

import java.util.function.Function;

/**
 * A utility used to hold generic methods and fields
 * that can be applied in multiple places.
 */
public class GeneralUtil {

    /**
     * Constructs a function to get an enum from a string.
     * The function will return null if the string is null
     * or the string does not match one of the existing
     * enum names.
     *
     * @param enumClass The enum class
     * @param toString The function to get a string from an enum
     * @param <E> The enum type
     * @return A {@link CallableFunction} that takes a string and returns an enum
     */
    public static <E extends Enum<E>> CallableFunction<? super String, ? extends E> stringToEnum(Class<E> enumClass, Function<? super E, ? extends String> toString) {
        return str -> {
            if (str == null) return null;
            for (E e : enumClass.getEnumConstants()) {
                if (toString.apply(e).equalsIgnoreCase(str))
                    return e;
            }
            return null;
        };
    }
}
