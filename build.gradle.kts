@file:Suppress("PropertyName", "SpellCheckingInspection", "DEPRECATION")

import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.20" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    // TabooLib 配置
    configure<TabooLibExtension> {
        description {
            name(rootProject.name)
        }
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
        version { taboolib = "6.2.2" }
    }

    // 仓库
    repositories {
        mavenCentral()
    }
    // 依赖
    dependencies {
        compileOnly(kotlin("stdlib"))
    }

    // 编译配置
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}