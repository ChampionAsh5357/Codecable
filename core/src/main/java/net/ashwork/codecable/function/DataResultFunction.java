/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.function;

import com.mojang.serialization.DataResult;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An extension of a {@link Function} for safely handling an exception thrown
 * within a codec.
 *
 * @param function a function which takes an input and returns an output or throws
 *                 an exception
 * @param errorMessage a message to display from a {@link DataResult }when an
 *                     exception is thrown within the function
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 */
public record DataResultFunction<T, R>(ThrowingFunction<T, R> function,
                                       BiFunction<T, Exception, String> errorMessage) implements Function<T, DataResult<R>> {

    /**
     * Convenience constructor for returning the stringified exception as the error
     * message.
     *
     * @param function a function which takes an input and returns an output or
     *                 throws an exception
     */
    public DataResultFunction(final ThrowingFunction<T, R> function) {
        this(function, (t, e) -> e.toString());
    }

    @Override
    public DataResult<R> apply(final T t) {
        try {
            return DataResult.success(function.apply(t));
        } catch (final Exception e) {
            return DataResult.error(this.errorMessage.apply(t, e));
        }
    }
}
