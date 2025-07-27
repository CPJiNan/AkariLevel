import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.25"
    kotlin("jvm") version "2.2.0"
}

taboolib {
    env {
        install(
            Metrics,
            CommandHelper,
            Kether,
            Bukkit,
            BukkitUI,
            BukkitHook,
            BukkitUtil,
        )
    }
    description {
        contributors { name("CPJiNan") }
    }
    version { taboolib = "6.2.3-ac49c9a" }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
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