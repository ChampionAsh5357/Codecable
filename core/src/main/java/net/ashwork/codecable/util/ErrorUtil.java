/*
 * Codecable
 * Copyright (c) 2021-2021 ChampionAsh5357.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.ashwork.codecable.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.ashwork.functionality.callable.CallableFunction;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * A simple error utility that holds helpers for
 * handling edge cases and silent exceptions within
 * codecs.
 */
public final class ErrorUtil {

    /**
     * Handles an uncaught exception within a codec and properly returns the errored state.
     *
     * @param objectClass The new object's class
     * @param codec The original codec being mapped
     * @param to The function to decode the original codec object to the new object
     * @param from The function to encode the new codec object to the original codec object
     * @param <A> The original type of the object
     * @param <S> The new type of the object
     * @return A {@link Codec} of the new object type
     * @see #flatXmapHandleUncaughtExceptions(String, Codec, CallableFunction, CallableFunction)
     */
    public static <A, S> Codec<S> flatXmapHandleUncaughtExceptions(final Class<S> objectClass, final Codec<A> codec,
                                                                   final CallableFunction<? super A, ? extends DataResult<? extends S>> to,
                                                                   final CallableFunction<? super S, ? extends DataResult<? extends A>> from) {
        return flatXmapHandleUncaughtExceptions(objectClass.getSimpleName(), codec, to, from);
    }

    /**
     * Handles an uncaught exception within a codec and properly returns the errored state.
     * 
     * @param objectName The name of the object
     * @param codec The original codec being mapped
     * @param to The function to decode the original codec object to the new object
     * @param from The function to encode the new codec object to the original codec object
     * @param <A> The original type of the object
     * @param <S> The new type of the object
     * @return A {@link Codec} of the new object type
     * @see Codec#flatXmap(Function, Function) 
     */
    @SuppressWarnings("unchecked")
    public static <A, S> Codec<S> flatXmapHandleUncaughtExceptions(final String objectName, final Codec<A> codec,
                                                                   final CallableFunction<? super A, ? extends DataResult<? extends S>> to,
                                                                   final CallableFunction<? super S, ? extends DataResult<? extends A>> from) {
        return codec.flatXmap(a -> handleUncaughtExceptions(objectName, "decode", () -> (DataResult<S>) to.apply(a)), s -> handleUncaughtExceptions(objectName, "encode", () -> (DataResult<A>) from.apply(s)));
    }

    /**
     * Handles an uncaught exception and returns the error.
     * 
     * @param objectName The name of the object
     * @param action The action being performed
     * @param callable A supplied instance of the object that can handle exceptions
     * @param <T> The coded type
     * @return A {@link DataResult} containing the error message.
     */
    public static <T> DataResult<T> handleUncaughtExceptions(final String objectName, final String action,
                                                             final Callable<DataResult<T>> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            return DataResult.error(constructErrorMessage(objectName, action, e));
        }
    }

    /**
     * Handles any uncaught exceptions by returning null.
     *
     * @param callable A Supplied instance of the object that can handle exceptions
     * @param <T> The object type
     * @return The object, or null if an exception was thrown
     */
    @Nullable
    public static <T> T nullCall(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Constructs a generic error message prefix and then supplies the error message.
     * 
     * @param objectName The object the action is being performed on
     * @param action The action being performed (e.g. 'encode', 'decode')
     * @param throwable The thrown error
     * @return The constructed error message
     * @see #constructErrorMessage(String, String, String)
     */
    public static String constructErrorMessage(final String objectName, final String action,
                                               final Throwable throwable) {
        return constructErrorMessage(objectName, action, stackTraceToString(throwable));
    }

    /**
     * Constructs a generic error message prefix and then supplies the error message.
     *
     * @param objectName The object the action is being performed on
     * @param action The action being performed (e.g. 'encode', 'decode')
     * @param message The error message
     * @return The constructed error message
     */
    public static String constructErrorMessage(final String objectName, final String action,
                                               final String message) {
        return "An exception was thrown while trying to " + action + " a " + objectName + ": " + message;
    }

    /**
     * Writes a stack trace to a string.
     *
     * @param throwable The thrown error
     * @return The stringified stack trace.
     */
    private static String stackTraceToString(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
