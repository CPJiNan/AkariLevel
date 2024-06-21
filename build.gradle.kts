import io.izzel.taboolib.gradle.*

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.12"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

taboolib {
    env {
        install(
            UNIVERSAL,
            UI,
            KETHER,
            METRICS,
            DATABASE,
            EXPANSION_JAVASCRIPT,
            BUKKIT_ALL
        )
    }
    description {
        contributors { name("CPJiNan") }
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("MythicMobs").optional(true)
            name("SX-Attribute").optional(true)
            name("OriginAttribute").optional(true)
        }
    }
    version { taboolib = "6.1.1" }
    relocate("kotlinx.serialization", "kotlinx1922.serialization162")
}

repositories {
    mavenCentral()
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11902:11902-minimize:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.2")
    compileOnly("io.lumine.xikage:MythicMobs:4.11.0@jar")
    compileOnly("io.lumine:Mythic-Dist:5.3.5@jar")
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}