 import java.util.Properties

/* Plugins */
plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("7.1.2")
    kotlin("jvm").version("1.8.0")
}

buildscript {
    repositories { gradlePluginPortal() }
    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman", "shadow", "7.1.2")
    }
}

/* Project Setting */
allprojects {
    group = "org.caramel.backas"
    version = property("projectVersion") as String // from gradle.properties
    description = "Noah Minecraft plugin"
}

dependencies {
    /* Daydream API */
    compileOnly("moe.caramel", "daydream-api", "1.19.3-R0.1-SNAPSHOT")

    /* PacketEvent */
    compileOnly("com.github.retrooper.packetevents", "spigot", "2.0.0-SNAPSHOT") {
        exclude("net.kyori")
        exclude("com.google.code.gson", "gson")
    }

    /* Lombok */
    val lombokVersion = "1.18.24"
    compileOnly("org.projectlombok", "lombok", lombokVersion)
    annotationProcessor("org.projectlombok", "lombok", lombokVersion)
    testCompileOnly("org.projectlombok", "lombok", lombokVersion)
    testAnnotationProcessor("org.projectlombok", "lombok", lombokVersion)

    /* FullMetalJacket API */
    compileOnly("moe.caramel", "fmj-api", "2.6.2")

    /* caramelLibraryLegacy API */
    compileOnly("moe.caramel", "caramellibrarylegacy", "1.0.0")

    /* (Compile Only) kotlin-stdlib */
    compileOnly("org.jetbrains.kotlin", "kotlin-stdlib", "1.8.0")

    /* Jar Libraries */
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

val targetJavaVersion = 17
java {
    val javaVersion: JavaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    build { dependsOn(shadowJar) }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version, "description" to project.description)
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(targetJavaVersion)
    }

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveClassifier.set("")
        relocate("io.github.retrooper.packetevents", "moe.caramel.common.shaded.io.github.retrooper.packetevents")
        relocate("com.github.retrooper.packetevents", "moe.caramel.common.shaded.com.github.retrooper.packetevents")
        base
    }

    withType<Jar> {
        val names = project.gradle.startParameter.taskNames;
        val name = if (names.size == 0) "build" else names[0]

        val exc = "The build path doesn't exist. Build it on the default path."
        val localProperties = project.rootProject.file("local.properties")
        try {
            val properties = Properties()
            val stream = localProperties.inputStream()
            properties.load(stream)

            val buildDir = properties.getProperty("${name}Dir")
            if (buildDir != null && buildDir.isNotBlank()) {
                this.destinationDirectory.set(file(buildDir))
            } else println(exc)
            stream.close()
        } catch (ignored: Exception) {
            localProperties.writeText("buildDir=\ndeployDir=\n")
            println(exc)
        }
    }

    task("deploy") {
        group = "deploy tool"
        dependsOn(build)
    }
}
