import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.apache.tools.ant.filters.*
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.Properties
import java.io.FileInputStream
import org.gradle.api.tasks.bundling.Jar
import java.net.URI

plugins {
    kotlin("jvm") version "1.2.60"
    id("maven-publish")
}

group = "fr.rhaz.minecraft"
version = "4.1"
val pname = "Sockets4MC"
val desc = "No more Plugin Messaging Channels"

repositories {
    mavenCentral()
    maven { url = URI("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = URI("https://repo.spongepowered.org/maven")}
    maven { url = URI("https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/")}
}

dependencies {
    compileOnly("fr.rhaz.minecraft:kotlin4mc:2.0.7")
    compileOnly("net.md-5:bungeecord-api:1.13-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    testCompileOnly(kotlin("stdlib-jdk8"))
    testCompileOnly("net.md-5:bungeecord-api:1.13-SNAPSHOT")
    testCompileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compile("fr.rhaz:sockets:3.0.7")
}

(tasks.getByName("processResources") as ProcessResources).apply {
    from("resources")
    val tokens = mapOf("id" to rootProject.name, "name" to pname, "version" to version, "desc" to desc)
    filter<ReplaceTokens>("tokens" to tokens)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val jar by tasks.getting(Jar::class) {
    destinationDir = file("$rootDir/jar")
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}

java.sourceSets {
    getByName("main").java {
        srcDirs("src")
        exclude("Kotlin4MC.kt")
    }
    getByName("test").java.srcDirs("test")
}

publishing {
    repositories {
        maven {
            val mavenwrite by System.getProperties()
            url = uri(mavenwrite)
        }
    }

    publications {
        val mavenJava by creating(MavenPublication::class) {
            from(components["java"])
        }
    }
}