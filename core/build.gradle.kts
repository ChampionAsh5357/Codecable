// Add plugins
plugins {
    `java-library`
    id("org.cadixdev.licenser")
}

// Set properties
base.archivesName.set(rootProject.extra["base.id"] as String)
version = rootProject.extra["core.version"] as String

// Add dependencies
dependencies {
    api(group = "com.mojang", name = "datafixerupper", version = rootProject.extra["dfu.version"] as String)
}
