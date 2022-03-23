// Set the name of the root project
rootProject.name = extra["base.id"] as String

// Set plugin versions
pluginManagement {
    plugins {
        id("org.cadixdev.licenser") version (extra["licenser.version"] as String)
        id("org.spongepowered.gradle.vanilla") version (extra["vanilla.gradle.version"] as String)
        id("fabric-loom") version (extra["loom.version"] as String)
    }

    resolutionStrategy.eachPlugin {
        if (requested.id.toString() == "net.minecraftforge.gradle")
            useModule(mapOf(
                "group" to requested.id.toString(),
                "name" to "ForgeGradle",
                "version" to (extra["forge.gradle.version"] as String)
            ))
    }

    repositories {
        gradlePluginPortal()
        maven {
            name = "Sponge"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net/")
        }
    }
}

internal val projectNames: Map<String, List<String>> = mapOf(
    (extra["minecraft.id"] as String) to listOf(
        extra["minecraft.core.id"] as String,
        extra["minecraft.fabric.id"] as String,
        extra["minecraft.forge.id"] as String
    )
)

// Add Project Builds
include(extra["core.id"] as String)
projectNames.forEach { (key, values) ->
    values.forEach {
        include("${key}:${key}-${it}")
    }
}
