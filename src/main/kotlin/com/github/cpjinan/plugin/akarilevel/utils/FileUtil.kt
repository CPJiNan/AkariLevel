package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import org.bukkit.Bukkit
import java.io.File

object FileUtil {
    val dataFolder =
        Bukkit.getPluginManager().getPlugin(AkariLevel.plugin.name)?.dataFolder ?: throw IllegalArgumentException(
            "Unable to find plugin data folder."
        )

    fun getFile(dir: Any, deep: Boolean): List<File> {
        val result = mutableListOf<File>()
        traverseFile(dir, deep) { result.add(it) }
        return result
    }

    fun getFileName(dir: Any, deep: Boolean): List<String> {
        val result = mutableListOf<String>()
        traverseFile(dir, deep) { result.add(it.name) }
        return result
    }

    private fun traverseFile(dir: Any, deep: Boolean, action: (File) -> Unit) {
        val parent = when (dir) {
            is String -> File(dir)
            is File -> dir
            else -> throw IllegalArgumentException("Unsupported argument type. Expected either a String or File.")
        }

        fun innerTraverse(file: File) {
            if (file.isFile) {
                action(file)
            } else if (deep && file.isDirectory && file.listFiles()?.isNotEmpty() == true) {
                file.listFiles()?.forEach { innerTraverse(it) }
            }
        }
        parent.listFiles()?.forEach { innerTraverse(it) }
    }
}