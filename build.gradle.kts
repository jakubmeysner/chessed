import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.jakubmeysner.chessed"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.github.bhlangonijr:chesslib:1.3.3")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.8"
    }
}

tasks.processResources {
    expand("version" to version)
}

tasks.jar {
    archiveBaseName.set("Chessed")
}

tasks.shadowJar {
    archiveBaseName.set("Chessed")
    archiveClassifier.set("")
}

tasks.test {
    useJUnitPlatform()
}
