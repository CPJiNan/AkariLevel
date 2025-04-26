@file:Suppress("DEPRECATION")

import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.23"
    kotlin("jvm") version "2.1.20"
}

taboolib {
    env {
        install(
            Metrics,
            Database,
            CommandHelper,
            Kether,
            JavaScript,
            Bukkit,
            BukkitHook,
            BukkitUtil
        )
    }
    description {
        contributors { name("CPJiNan") }
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("MythicMobs").optional(true)
            name("SX-Attribute").optional(true)
            name("OriginAttribute").optional(true)
            name("DungeonPlus").optional(true)
        }
    }
    version { taboolib = "6.2.3-8cc2f66" }
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
    compileOnly("io.lumine.xikage:MythicMobs:4.11.0@jar")
    compileOnly("io.lumine:Mythic-Dist:5.3.5@jar")
    compileOnly(fileTree("libs"))
    compileOnly("org.openjdk.nashorn:nashorn-core:15.6")
    compileOnly("com.google.code.gson:gson:2.12.1")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        freeCompilerArgs.set(listOf("-Xjvm-default=all", "-Xextended-compiler-checks"))
    }
}