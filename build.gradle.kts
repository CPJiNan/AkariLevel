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
            Bukkit,
            BukkitUI,
            BukkitHook,
            BukkitUtil
        )
    }
    description {
        contributors {
            name("CPJiNan")
            name("QwQ-dev")
        }
    }
    version { taboolib = "6.2.3-6bdc1c7" }
    relocate("top.maplex.arim", "com.github.cpjinan.plugin.akarilevel.arim")
    relocate("com.github.benmanes.caffeine", "com.github.cpjinan.plugin.akarilevel.caffeine")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    taboo("top.maplex.arim:Arim:1.2.14")
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