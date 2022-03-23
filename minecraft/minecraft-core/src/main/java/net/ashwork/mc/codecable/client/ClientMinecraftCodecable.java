/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.mc.codecable.client;

import com.mojang.serialization.Codec;
import net.ashwork.mc.codecable.MinecraftCodecable;
import net.ashwork.mc.codecable.client.primitive.ClientMinecraftWrapperCodec;

/**
 * An extension of {@link MinecraftCodecable} and its operations.
 *
 * @param <A> the type of the object
 */
public interface ClientMinecraftCodecable<A> extends MinecraftCodecable<A> {

    /**
     * Wraps a codec to make it an instance of {@code ClientMinecraftCodecable}.
     *
     * @param codec the codec delegate to wrap
     * @param <A> the type of the object
     * @return a wrapped codec
     */
    static <A> ClientMinecraftCodecable<A> wrap(final Codec<A> codec) {
        return new ClientMinecraftWrapperCodec<>(codec);
    }

    //TODO Implement
    /* 1.18.2
    - LayerDefinition
        - MeshDefinition
            - PartDefinition (root)
                - String name
                - CubeListBuilder cubes
                    - boolean mirror g6
                    - int textureOffsetX g12
                    - int textureOffsetY g12
                    - String name 1245
                    - float originX 12345678
                    - float originY 12345678
                    - float originZ 12345678
                    - int/float dimensionX 12/345678
                    - int/float dimensionY 12/345678
                    - int/float dimensionZ 12/345678
                    - CubeDeformation grow 1578
                        - float growX
                        - float growY
                        - float growZ
                    - int textureScaleX 7
                    - int textureScaleY 7
                - PartPose pose
                    - float x
                    - float y
                    - float z
                    - float xRot
                    - float yRot
                    - float zRot
        - int textureWidth
        - int textureHeight
     */

    //TODO Implement
    /* 22w11a
        - AnimationDefinition
            - float lengthInSeconds
            - boolean looping
            - Map<String, List<AnimationChannel>> boneAnimations
                - AnimationChannel$Target target
                - List<KeyFrame>
                    - float timestamp
                    - Vector3f target
                    - AnimationChannel$Interpolation interpolation
     */
}
