import java.util.Calendar
import org.cadixdev.gradle.licenser.Licenser
import java.time.Instant
import java.time.format.DateTimeFormatter

// Add plugins
plugins {
    java
    id("org.cadixdev.licenser")
}

// Cache some properties
internal val artifactGroup = extra["base.group"] as String
internal val fileEncoding = extra["file.encoding"] as String
internal val jUnitVersion: String = extra["junit.version"] as String
internal val headerFile: TextResource = resources.text.fromFile(rootProject.file("HEADER"))
internal val author = extra["base.author"] as String
internal val projectArtifact = extra["base.id"] as String
internal val projectName = extra["base.name"] as String
internal val year: Int = Calendar.getInstance().get(Calendar.YEAR)
internal val javaVersion = extra["java.version"] as String

subprojects {
    // Java Settings
    plugins.withType<JavaPlugin> {
        group = artifactGroup
        java {
            toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))

            withSourcesJar()
            withJavadocJar()
        }
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = fileEncoding
        }
        tasks.javadoc {
            options {
                encoding = fileEncoding
                if (this is StandardJavadocDocletOptions)
                    tags(
                        "apiNote:a:API Note:",
                        "implSpec:a:Implementation Requirements:",
                        "implNote:a:Implementation Note:"
                    )
            }
        }

        // Add common repositories
        repositories {
            mavenCentral()
            maven {
                name = "Minecraft Libraries"
                url = uri("https://libraries.minecraft.net")
            }
        }

        // Manifest Attributes
        afterEvaluate {
            tasks.jar {
                from(rootProject.file("LICENSE")) {
                    expand(
                        mapOf(
                            "year" to year,
                            "author" to author
                        )
                    )
                }

                manifest.attributes(mapOf(
                    "Specification-Title" to projectArtifact,
                    "Specification-Vendor" to author,
                    "Specification-Version" to (project.version as String).split('-')[0],
                    "Implementation-Title" to base.archivesName.get(),
                    "Implementation-Vendor" to author,
                    "Implementation-Version" to project.version,
                    "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                ))
            }
        }

        // Unit Tests
        repositories {
            mavenCentral()
        }
        tasks.test {
            useJUnitPlatform()
        }
        dependencies {
            testImplementation(platform(mapOf(
                "group" to "org.junit",
                "name" to "junit-bom",
                "version" to jUnitVersion
            )))
            testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
        }
    }

    // License Settings
    plugins.withType<Licenser> {
        license {
            header.set(headerFile)
            properties {
                set("projectName", projectName)
                set("author", author)
            }
            include("**/*.java")
        }
    }
}
