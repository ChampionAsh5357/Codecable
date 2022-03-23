/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable;

import com.google.common.collect.BiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.ashwork.codecable.collection.SetCodec;
import net.ashwork.codecable.function.DataResultFunction;
import net.ashwork.codecable.function.ThrowingFunction;
import net.ashwork.codecable.map.SimpleBiMapCodec;
import net.ashwork.codecable.map.SimpleImprovedMapCodec;
import net.ashwork.codecable.map.UnboundedBiMapCodec;
import net.ashwork.codecable.map.UnboundedImprovedMapCodec;
import net.ashwork.codecable.primitive.EnumCodec;
import net.ashwork.codecable.primitive.WrapperCodec;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

//TODO: Look over how partial results should be handled
//TODO: Look over what the encoded type of the pair should hold on decode

/**
 * An extension of {@link Codec} and its operations.
 *
 * @param <A> the type of the object
 */
public interface Codecable<A> extends Codec<A> {

    /**
     * Wraps a codec to make it an instance of {@code Codecable}.
     *
     * @param codec the codec delegate to wrap
     * @param <A> the type of the object
     * @return a wrapped codec
     */
    static <A> Codecable<A> wrap(final Codec<A> codec) {
        return new WrapperCodec<>(codec);
    }

    /**
     * Creates a key-compressible map {@link MapCodec}. When an error is found, the
     * map will continue reading the data and supply the result in the partial
     * stored within the errored {@link DataResult}.
     *
     * @apiNote
     * This is an improved form of {@link SimpleMapCodec} which makes maps more
     * resistant to errored entries as the user specifies.
     *
     * @param keyCodec a codec for the keys of the map
     * @param valueCodec a codec for the values of the map
     * @param keys a list of keys that this map can have
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a key-compressible map {@link MapCodec}
     *
     * @see Codec#simpleMap(Codec, Codec, Keyable)
     */
    static <K, V> MapCodec<Map<K, V>> improvedSimpleMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys) {
        return improvedSimpleMap(keyCodec, valueCodec, keys, false);
    }

    /**
     * Creates a key-compressible map {@link MapCodec}.
     *
     * @apiNote
     * This is an improved form of {@link SimpleMapCodec} which makes maps more
     * resistant to errored entries as the user specifies.
     *
     * @param keyCodec a codec for the keys of the map
     * @param valueCodec a codec for the values of the map
     * @param keys a list of keys that this map can have
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           map as soon as an error is found. The map will
     *                           appear in the partial when the codec errors
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a key-compressible map {@link MapCodec}
     *
     * @see Codec#simpleMap(Codec, Codec, Keyable)
     */
    static <K, V> MapCodec<Map<K, V>> improvedSimpleMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys, final boolean stopOnFirstFailure) {
        return new SimpleImprovedMapCodec<>(keyCodec, valueCodec, keys, stopOnFirstFailure);
    }

    /**
     * Creates a map codec. When an error is found, the map will continue reading
     * the data and supply the result in the partial stored within the errored
     * {@link DataResult}.
     *
     * @apiNote
     * This is an improved form of {@link UnboundedMapCodec} which makes maps more
     * resistant to errored entries as the user specifies.
     *
     * @param keyCodec a codec for the keys of the map
     * @param valueCodec a codec for the values of the map
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a map codec
     *
     * @see Codec#unboundedMap(Codec, Codec)
     */
    static <K, V> Codecable<Map<K, V>> improvedUnboundedMap(final Codec<K> keyCodec, final Codec<V> valueCodec) {
        return improvedUnboundedMap(keyCodec, valueCodec, false);
    }

    /**
     * Creates a map codec.
     *
     * @apiNote
     * This is an improved form of {@link UnboundedMapCodec} which makes maps more
     * resistant to errored entries as the user specifies.
     *
     * @param keyCodec a codec for the keys of the map
     * @param valueCodec a codec for the values of the map
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           map as soon as an error is found. The map will
     *                           appear in the partial when the codec errors
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a map codec
     *
     * @see Codec#unboundedMap(Codec, Codec)
     */
    static <K, V> Codecable<Map<K, V>> improvedUnboundedMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final boolean stopOnFirstFailure) {
        return new UnboundedImprovedMapCodec<>(keyCodec, valueCodec, stopOnFirstFailure);
    }

    /**
     * Creates a key-compressible bimap {@link MapCodec}. When an error is found,
     * the bimap will continue reading the data and supply the result in the partial
     * stored within the errored {@link DataResult}. If a duplicate value is found,
     * the bimap will discard the duplicate entry.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param keys a list of keys that this bimap can have
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a key-compressible bimap {@link MapCodec}
     */
    static <K, V> MapCodec<BiMap<K, V>> simpleBiMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys) {
        return simpleBiMap(keyCodec, valueCodec, keys, false);
    }

    /**
     * Creates a key-compressible bimap {@link MapCodec}. When an error is found,
     * the bimap will continue reading the data and supply the result in the partial
     * stored within the errored {@link DataResult}.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param keys a list of keys that this bimap can have
     * @param failOnDuplicate if {@code true}, any duplicate values will return an
     *                        errored {@link DataResult}
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a key-compressible bimap {@link MapCodec}
     */
    static <K, V> MapCodec<BiMap<K, V>> simpleBiMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys, final boolean failOnDuplicate) {
        return simpleBiMap(keyCodec, valueCodec, keys, failOnDuplicate, false);
    }

    /**
     * Creates a key-compressible bimap {@link MapCodec}.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param keys a list of keys that this bimap can have
     * @param failOnDuplicate if {@code true}, any duplicate values will return an
     *                        errored {@link DataResult}
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           bimap as soon as an error is found. The bimap will
     *                           appear in the partial when the codec errors
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a key-compressible bimap {@link MapCodec}
     */
    static <K, V> MapCodec<BiMap<K, V>> simpleBiMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final Keyable keys, final boolean failOnDuplicate, final boolean stopOnFirstFailure) {
        return new SimpleBiMapCodec<>(keyCodec, valueCodec, keys, failOnDuplicate, stopOnFirstFailure);
    }

    /**
     * Creates a bimap codec. When an error is found, the bimap will continue
     * reading the data and supply the result in the partial stored within the
     * errored {@link DataResult}. If a duplicate value is found, the bimap will
     * discard the duplicate entry.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a bimap codec
     */
    static <K, V> Codecable<BiMap<K, V>> unboundedBiMap(final Codec<K> keyCodec, final Codec<V> valueCodec) {
        return unboundedBiMap(keyCodec, valueCodec, false);
    }

    /**
     * Creates a bimap codec. When an error is found, the bimap will continue
     * reading the data and supply the result in the partial stored within the
     * errored {@link DataResult}.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param failOnDuplicate if {@code true}, any duplicate values will return an
     *                        errored {@link DataResult}
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a bimap codec
     */
    static <K, V> Codecable<BiMap<K, V>> unboundedBiMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final boolean failOnDuplicate) {
        return unboundedBiMap(keyCodec, valueCodec, failOnDuplicate, false);
    }

    /**
     * Creates a bimap codec.
     *
     * @param keyCodec a codec for the keys of the bimap
     * @param valueCodec a codec for the values of the bimap
     * @param failOnDuplicate if {@code true}, any duplicate values will return an
     *                        errored {@link DataResult}
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           bimap as soon as an error is found. The bimap will
     *                           appear in the partial when the codec errors
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a bimap codec
     */
    static <K, V> Codecable<BiMap<K, V>> unboundedBiMap(final Codec<K> keyCodec, final Codec<V> valueCodec, final boolean failOnDuplicate, final boolean stopOnFirstFailure) {
        return new UnboundedBiMapCodec<>(keyCodec, valueCodec, failOnDuplicate, stopOnFirstFailure);
    }

    /**
     * Creates a set codec. When an error is found, the set will continue reading
     * the data and supply the result in the partial stored within the errored
     * {@link DataResult}. If a duplicate value is found, the set will discard the
     * duplicate element.
     *
     * @param elementCodec a codec for the elements of the set
     * @param <E> the type of the element
     * @return a set codec
     */
    static <E> Codecable<Set<E>> set(final Codec<E> elementCodec) {
        return set(elementCodec, false);
    }

    /**
     * Creates a set codec. When an error is found, the set will continue reading
     * the data and supply the result in the partial stored within the errored
     * {@link DataResult}.
     *
     * @param elementCodec a codec for the elements of the set
     * @param failOnDuplicate if {@code true}, any duplicate elements will return an
     *                        errored {@link DataResult}
     * @param <E> the type of the element
     * @return a set codec
     */
    static <E> Codecable<Set<E>> set(final Codec<E> elementCodec, final boolean failOnDuplicate) {
        return set(elementCodec, failOnDuplicate, false);
    }

    /**
     * Creates a set codec.
     *
     * @param elementCodec a codec for the elements of the set
     * @param failOnDuplicate if {@code true}, any duplicate elements will return an
     *                        errored {@link DataResult}
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           set as soon as an error is found. The set will
     *                           appear in the partial when the codec errors
     * @param <E> the type of the element
     * @return a set codec
     */
    static <E> Codecable<Set<E>> set(final Codec<E> elementCodec, final boolean failOnDuplicate, final boolean stopOnFirstFailure) {
        return new SetCodec<>(elementCodec, failOnDuplicate, stopOnFirstFailure);
    }

    /**
     * Creates an enum codec. Encodes the enum using its lowercase name via
     * {@link Enum#name()} and decodes by checking the string ignoring cases.
     * When compressed, encodes and decodes the enum using its ordinal via
     * {@link Enum#ordinal()}.
     *
     * @param enumClass the class of the enum
     * @param <E> the type of the enum
     * @return an enum codec
     */
    static <E extends Enum<E>> Codecable<E> enumOf(final Class<E> enumClass) {
        return enumOf(enumClass, s -> Arrays.stream(enumClass.getEnumConstants())
                        .filter(e -> s.equalsIgnoreCase(e.name())).findFirst().orElseThrow(),
                e -> e.name().toLowerCase(Locale.ROOT));
    }

    /**
     * Creates an enum codec. When compressed, encodes and decodes the enum using
     * its ordinal via {@link Enum#ordinal()}.
     *
     * @param enumClass the class of the enum
     * @param fromString a function that transforms a string into an enum or throws
     *                   an exception
     * @param toString a function that transforms an enum into a string or throws
     *                 an exception
     * @param <E> the type of the enum
     * @return an enum codec
     */
    static <E extends Enum<E>> Codecable<E> enumOf(final Class<E> enumClass, final ThrowingFunction<String, E> fromString, final Function<E, String> toString) {
        return enumOf(fromString, toString, i -> enumClass.getEnumConstants()[i], Enum::ordinal);
    }

    /**
     * Creates an enum codec.
     *
     * @param fromString a function that transforms a string into an enum or throws
     *                   an exception
     * @param toString a function that transforms an enum into a string or throws
     *                 an exception
     * @param fromInt a function that transforms an {@code int} into an enum or
     *                throws an exception
     * @param toInt a function that transforms an enum into an {@code int} or throws
     *              an exception
     * @param <E> the type of the enum
     * @return an enum codec
     */
    static <E extends Enum<E>> Codecable<E> enumOf(final ThrowingFunction<String, E> fromString, final Function<E, String> toString, final ThrowingFunction<Integer, E> fromInt, final ToIntFunction<E> toInt) {
        return new EnumCodec<>(fromString, toString, fromInt, toInt);
    }

    /**
     * Creates an offset time codec.
     *
     * @param formatter a formatter to encode and decode the offset time
     * @return an offset time codec
     */
    static Codecable<OffsetTime> offsetTime(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, OffsetTime::from);
    }

    /**
     * Creates a Japanese date codec.
     *
     * @param formatter a formatter to encode and decode the Japanese date
     * @return a Japanese date codec
     */
    static Codecable<JapaneseDate> japaneseDate(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, JapaneseDate::from);
    }

    /**
     * Creates a Hijrah date codec.
     *
     * @param formatter a formatter to encode and decode the Hijrah date
     * @return a Hijrah date codec
     */
    static Codecable<HijrahDate> hijrahDate(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, HijrahDate::from);
    }

    /**
     * Creates a Minguo date codec.
     *
     * @param formatter a formatter to encode and decode the Minguo date
     * @return a Minguo date codec
     */
    static Codecable<MinguoDate> minguoDate(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, MinguoDate::from);
    }

    /**
     * Creates a Thai Buddhist date codec.
     *
     * @param formatter a formatter to encode and decode the Thai Buddhist date
     * @return a Thai Buddhist date codec
     */
    static Codecable<ThaiBuddhistDate> thaiBuddhistDate(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, ThaiBuddhistDate::from);
    }

    /**
     * Creates a zoned date time codec.
     *
     * @param formatter a formatter to encode and decode the zoned date time
     * @return a zoned date time codec
     */
    static Codecable<ZonedDateTime> zonedDateTime(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, ZonedDateTime::from);
    }

    /**
     * Creates a local date time codec.
     *
     * @param formatter a formatter to encode and decode the local date time
     * @return a local date time codec
     */
    static Codecable<LocalDateTime> localDateTime(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, LocalDateTime::from);
    }

    /**
     * Creates an instant codec.
     *
     * @param formatter a formatter to encode and decode the instant
     * @return an instant codec
     */
    static Codecable<Instant> instant(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, Instant::from);
    }

    /**
     * Creates an offset date time codec.
     *
     * @param formatter a formatter to encode and decode the offset date time
     * @return an offset date time codec
     */
    static Codecable<OffsetDateTime> offsetDateTime(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, OffsetDateTime::from);
    }

    /**
     * Creates a year codec.
     *
     * @param formatter a formatter to encode and decode the year
     * @return a year codec
     */
    static Codecable<Year> year(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, Year::from);
    }

    /**
     * Creates a local date codec.
     *
     * @param formatter a formatter to encode and decode the local date
     * @return a local date codec
     */
    static Codecable<LocalDate> localDate(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, LocalDate::from);
    }

    /**
     * Creates a local time codec.
     *
     * @param formatter a formatter to encode and decode the local time
     * @return a local time codec
     */
    static Codecable<LocalTime> localTime(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, LocalTime::from);
    }

    /**
     * Creates a year month codec.
     *
     * @param formatter a formatter to encode and decode the year month
     * @return a year month codec
     */
    static Codecable<YearMonth> yearMonth(final DateTimeFormatter formatter) {
        return temporalAccessor(formatter, YearMonth::from);
    }

    /**
     * Creates a temporal accessor codec.
     *
     * @param formatter a formatter to encode and decode the temporal accessor
     * @param query a function that transforms a temporal accessor to its exact type
     * @param <T> the type of the temporal accessor
     * @return a temporal accessor codec
     */
    static <T extends TemporalAccessor> Codecable<T> temporalAccessor(final DateTimeFormatter formatter, final TemporalQuery<T> query) {
        return wrap(Codec.STRING.flatXmap(new DataResultFunction<>(s -> formatter.parse(s, query)), new DataResultFunction<>(formatter::format)));
    }

    /**
     * Creates a set codec from this codec. When an error is found, the set will
     * continue reading the data and supply the result in the partial stored within
     * the errored {@link DataResult}. If a duplicate value is found, the set will
     * discard the duplicate element.
     *
     * @return a set codec
     */
    default Codecable<Set<A>> setOf() {
        return set(this);
    }

    /**
     * Creates a set codec from this codec. When an error is found, the set will
     * continue reading the data and supply the result in the partial stored within
     * the errored {@link DataResult}.
     *
     * @param failOnDuplicate if {@code true}, any duplicate elements will return an
     *                        errored {@link DataResult}
     * @return a set codec
     */
    default Codecable<Set<A>> setOf(final boolean failOnDuplicate) {
        return set(this, failOnDuplicate);
    }

    /**
     * Creates a set codec from this codec.
     *
     * @param failOnDuplicate if {@code true}, any duplicate elements will return an
     *                        errored {@link DataResult}
     * @param stopOnFirstFailure if {@code true}, the codec will stop reading the
     *                           set as soon as an error is found. The set will
     *                           appear in the partial when the codec errors
     * @return a set codec
     */
    default Codecable<Set<A>> setOf(final boolean failOnDuplicate, final boolean stopOnFirstFailure) {
        return set(this, failOnDuplicate, stopOnFirstFailure);
    }

    /**
     * An offset time codec encoded using {@link DateTimeFormatter#ISO_OFFSET_TIME}.
     *
     * @see DateTimeFormatter#ISO_OFFSET_TIME
     */
    Codecable<OffsetTime> OFFSET_TIME = offsetTime(DateTimeFormatter.ISO_OFFSET_TIME);

    /**
     * A zoned date time codec encoded using {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}.
     *
     * @see DateTimeFormatter#ISO_ZONED_DATE_TIME
     */
    Codecable<ZonedDateTime> ZONED_DATE_TIME = zonedDateTime(DateTimeFormatter.ISO_ZONED_DATE_TIME);

    /**
     * A local date time codec encoded using {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @see DateTimeFormatter#ISO_LOCAL_DATE_TIME
     */
    Codecable<LocalDateTime> LOCAL_DATE_TIME = localDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    /**
     * An instant codec encoded using {@link DateTimeFormatter#ISO_INSTANT}.
     *
     * @see DateTimeFormatter#ISO_INSTANT
     */
    Codecable<Instant> INSTANT = instant(DateTimeFormatter.ISO_INSTANT);

    /**
     * An offset date time codec encoded using {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}.
     *
     * @see DateTimeFormatter#ISO_OFFSET_DATE_TIME
     */
    Codecable<OffsetDateTime> OFFSET_DATE_TIME = offsetDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    /**
     * A local date codec encoded using {@link DateTimeFormatter#ISO_LOCAL_DATE}.
     *
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     */
    Codecable<LocalDate> LOCAL_DATE = localDate(DateTimeFormatter.ISO_LOCAL_DATE);

    /**
     * A local time codec encoded using {@link DateTimeFormatter#ISO_LOCAL_TIME}.
     *
     * @see DateTimeFormatter#ISO_LOCAL_TIME
     */
    Codecable<LocalTime> LOCAL_TIME = localTime(DateTimeFormatter.ISO_LOCAL_TIME);
}
