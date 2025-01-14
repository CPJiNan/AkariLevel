@file:Suppress("DEPRECATION")

gradle.buildFinished {
    buildDir.deleteRecursively()
}