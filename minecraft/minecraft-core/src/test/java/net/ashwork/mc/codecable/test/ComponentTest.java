/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.ashwork.mc.codecable.MinecraftCodecable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * A test utility for component codecs.
 */
public final class ComponentTest {

    /**
     * Tests {@link MinecraftCodecable#MUTABLE_COMPONENT}.
     */
    @Test
    public void mutableComponent() {
        final TranslatableComponent translatable = new TranslatableComponent("test.test");
        final TextComponent text = new TextComponent("test");
        final Codec<MutableComponent> codec = MinecraftCodecable.MUTABLE_COMPONENT;

        Assertions.assertDoesNotThrow(() -> {
            final JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, translatable).getOrThrow(false, System.out::println);

            final MutableComponent r = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow(false, System.out::println);
            Assertions.assertEquals(translatable.getClass(), r.getClass());
            if (r instanceof TranslatableComponent t)
                Assertions.assertEquals(translatable.getKey(), t.getKey());
        });

        Assertions.assertDoesNotThrow(() -> {
            final Tag encoded = codec.encodeStart(NbtOps.INSTANCE, translatable).getOrThrow(false, System.out::println);

            final MutableComponent r = codec.parse(NbtOps.INSTANCE, encoded).getOrThrow(false, System.out::println);
            Assertions.assertEquals(translatable.getClass(), r.getClass());
            if (r instanceof TranslatableComponent t)
                Assertions.assertEquals(translatable.getKey(), t.getKey());
        });

        Assertions.assertDoesNotThrow(() -> {
            final JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, text).getOrThrow(false, System.out::println);

            final MutableComponent r = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow(false, System.out::println);
            Assertions.assertEquals(text.getClass(), r.getClass());
            if (r instanceof TextComponent t)
                Assertions.assertEquals(text.getText(), t.getText());
        });

        Assertions.assertDoesNotThrow(() -> {
            final Tag encoded = codec.encodeStart(NbtOps.INSTANCE, text).getOrThrow(false, System.out::println);

            final MutableComponent r = codec.parse(NbtOps.INSTANCE, encoded).getOrThrow(false, System.out::println);
            Assertions.assertEquals(text.getClass(), r.getClass());
            if (r instanceof TextComponent t)
                Assertions.assertEquals(text.getText(), t.getText());
        });

        Assertions.assertThrows(RuntimeException.class, () -> codec.parse(JsonOps.INSTANCE, new JsonObject()).getOrThrow(false, System.out::println));
        Assertions.assertThrows(RuntimeException.class, () -> codec.parse(NbtOps.INSTANCE, new CompoundTag()).getOrThrow(false, System.out::println));
    }
}
