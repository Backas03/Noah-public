import java.util.Properties

/* Plugins */
plugins {
    id("java")
}

/* Project Setting */
allprojects {
    group = "org.caramel.backas"
    version = property("projectVersion") as String // from gradle.properties
    description = "Noah Minecraft plugin"
}

dependencies {
    /* Daydream API */
    compileOnly("moe.caramel", "daydream-api", "1.17.1-R0.1-SNAPSHOT")

    /* Lombok */
    val lombokVersion = "1.18.24"
    compileOnly("org.projectlombok", "lombok", lombokVersion)
    annotationProcessor("org.projectlombok", "lombok", lombokVersion)
    testCompileOnly("org.projectlombok", "lombok", lombokVersion)
    testAnnotationProcessor("org.projectlombok", "lombok", lombokVersion)

    /* FullMetalJacket API */
    compileOnly("moe.caramel", "fmj-api", "2.6.1-SNAPSHOT")

    /* caramelLibraryLegacy API */
    compileOnly("moe.caramel", "caramellibrarylegacy", "1.0.0")

    /* ProtocolLib */
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0")
}

val targetJavaVersion = 17
java {
    val javaVersion: JavaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version, "description" to project.description)
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(targetJavaVersion)
    }

    withType<Jar> {
        val names = project.gradle.startParameter.taskNames;
        val name = if (names.size == 0) "build" else names[0]

        val exc = "The build path doesn't exist. Build it on the default path."
        val localProperties = project.rootProject.file("local.properties");
        try {
            val properties = Properties()
            val stream = localProperties.inputStream()
            properties.load(stream)

            val buildDir = properties.getProperty("${name}Dir");
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

