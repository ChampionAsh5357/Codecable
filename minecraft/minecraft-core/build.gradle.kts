// Add plugins
plugins {
    `java-library`
    id("org.cadixdev.licenser")
    id("org.spongepowered.gradle.vanilla")
}

// Set properties
base.archivesName.set(rootProject.extra["base.id"] as String)
group = rootProject.extra["minecraft.group"] as String
version = rootProject.extra["minecraft.core.version"] as String

// Add dependencies
dependencies {
    api(rootProject.project(":core"))
}

// Setup Minecraft
minecraft {
    version(extra["mc.version"] as String)
}
