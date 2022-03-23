/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.function;

import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result or throws
 * an exception.
 *
 * <p>This is a functional interface whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {

    /**
     * Applies this function to the given argument or throws an exception.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(final T t) throws Exception;
}
