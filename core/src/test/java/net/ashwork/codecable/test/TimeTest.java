/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.test;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ashwork.codecable.Codecable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

/**
 * A test utility for time-based codecs.
 */
public final class TimeTest {

    /**
     * Tests {@link Codecable#offsetTime(DateTimeFormatter)}.
     */
    @Test
    public void offsetTime() {
        runTimeTest(OffsetTime.now(), Codecable.OFFSET_TIME);
    }

    /**
     * Tests {@link Codecable#japaneseDate(DateTimeFormatter)}.
     */
    @Test
    public void japaneseDate() {
        runTimeTest(JapaneseDate.now(), Codecable.japaneseDate(DateTimeFormatter.BASIC_ISO_DATE));
    }

    /**
     * Tests {@link Codecable#hijrahDate(DateTimeFormatter)}.
     */
    @Test
    public void hijrahDate() {
        runTimeTest(HijrahDate.now(), Codecable.hijrahDate(DateTimeFormatter.ISO_DATE));
    }

    /**
     * Tests {@link Codecable#minguoDate(DateTimeFormatter)}.
     */
    @Test
    public void minguoDate() {
        runTimeTest(MinguoDate.now(), Codecable.minguoDate(DateTimeFormatter.ISO_ORDINAL_DATE));
    }

    /**
     * Tests {@link Codecable#thaiBuddhistDate(DateTimeFormatter)}.
     */
    @Test
    public void thaiBuddhistDate() {
        runTimeTest(ThaiBuddhistDate.now(), Codecable.thaiBuddhistDate(DateTimeFormatter.ISO_WEEK_DATE));
    }

    /**
     * Tests {@link Codecable#zonedDateTime(DateTimeFormatter)}.
     */
    @Test
    public void zonedDateTime() {
        runTimeTest(ZonedDateTime.now(), Codecable.ZONED_DATE_TIME);
    }

    /**
     * Tests {@link Codecable#localDateTime(DateTimeFormatter)}.
     */
    @Test
    public void localDateTime() {
        runTimeTest(LocalDateTime.now(), Codecable.LOCAL_DATE_TIME);
    }

    /**
     * Tests {@link Codecable#instant(DateTimeFormatter)}.
     */
    @Test
    public void instant() {
        runTimeTest(Instant.now(), Codecable.INSTANT);
    }

    /**
     * Tests {@link Codecable#offsetDateTime(DateTimeFormatter)}.
     */
    @Test
    public void offsetDateTime() {
        runTimeTest(OffsetDateTime.now(), Codecable.OFFSET_DATE_TIME);
    }

    /**
     * Tests {@link Codecable#year(DateTimeFormatter)}.
     */
    @Test
    public void year() {
        runTimeTest(Year.now(), Codecable.year(DateTimeFormatter.ofPattern("yyyy")));
    }

    /**
     * Tests {@link Codecable#localDate(DateTimeFormatter)}.
     */
    @Test
    public void localDate() {
        runTimeTest(LocalDate.now(), Codecable.LOCAL_DATE);
    }

    /**
     * Tests {@link Codecable#localTime(DateTimeFormatter)}.
     */
    @Test
    public void localTime() {
        runTimeTest(LocalTime.now(), Codecable.LOCAL_TIME);
    }

    /**
     * Tests {@link Codecable#yearMonth(DateTimeFormatter)}.
     */
    @Test
    public void yearMonth() {
        runTimeTest(YearMonth.now(), Codecable.yearMonth(DateTimeFormatter.ofPattern("MM-yyyy")));
    }

    /**
     * Creates a test for a temporal accessor. Verifies it can encode and decode
     * the data.
     *
     * @param instance a temporal accessor instance
     * @param codec the temporal accessor codec
     * @param <T> the type of the temporal accessor
     */
    private static <T extends TemporalAccessor> void runTimeTest(final T instance, final Codec<T> codec) {
        Assertions.assertDoesNotThrow(() -> {
            final JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, System.out::println);

            Assertions.assertEquals(instance,
                    Assertions.assertDoesNotThrow(() -> codec.parse(JsonOps.INSTANCE, encoded).getOrThrow(false, System.out::println)));
        });
    }
}
