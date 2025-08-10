package com.github.cpjinan.plugin.akarilevel.utils

import taboolib.platform.util.bukkitPlugin
import java.io.File

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.utils
 *
 * 文件工具类。
 *
 * @author 季楠
 * @since 2025/8/7 22:28
 */
object FileUtils {
    /**
     * 通过路径获取指定文件。
     *
     * 如果文件不存在，则创建该文件。
     *
     * @param path 要获取的文件路径。
     * @return 指定文件。
     */
    fun getFileOrCreate(path: String): File {
        return File(bukkitPlugin.dataFolder, path).apply {
            parentFile?.mkdirs()
            createNewFile()
        }
    }
}