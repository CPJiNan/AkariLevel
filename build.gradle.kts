import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.27"
    kotlin("jvm") version "2.2.0"
}

taboolib {
    env {
        install(
            Basic,
            Metrics,
            Database,
            CommandHelper,
            Kether,
            JavaScript,
            Bukkit,
            BukkitUI,
            BukkitHook,
            BukkitUtil
        )
    }
    description {
        contributors {
            name("CPJiNan")
        }
    }
    version { taboolib = "6.2.3-e102d76" }
    relocate("top.maplex.arim", "com.github.cpjinan.plugin.akarilevel.arim")
    relocate("com.github.benmanes.caffeine", "com.github.cpjinan.plugin.akarilevel.caffeine")
}

repositories {
    mavenCentral()
    // MythicMobs
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    // MythicMobs
    compileOnly("io.lumine.xikage:MythicMobs:4.11.0")
    compileOnly("io.lumine:Mythic-Dist:5.9.5")
    // nashorn
    compileOnly("org.openjdk.nashorn:nashorn-core:15.6")
    // Arim
    taboo("top.maplex.arim:Arim:1.2.14")
    // caffeine
    taboo("com.github.ben-manes.caffeine:caffeine:2.9.3")
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
        freeCompilerArgs.set(listOf("-Xjvm-default=all"))
    }
}