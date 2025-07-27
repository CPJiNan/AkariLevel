package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.utils
 *
 * @author 季楠
 * @since 2025/6/21 19:45
 */
object FileUtils {
    /**
     * 获取文件夹内所有文件
     * @param dir 待获取文件夹
     * @param deep 是否查找目标路径中文件夹内文件
     * @return 文件夹内所有文件
     */
    @JvmStatic
    fun getFile(dir: Any, deep: Boolean): ArrayList<File> {
        val result = arrayListOf<File>()
        forEachFile(dir, deep) { result.add(it) }
        return result
    }

    /**
     * 获取文件夹内所有文件名称
     * @param dir 待获取文件夹
     * @param deep 是否查找目标路径中文件夹内文件
     * @return 文件夹内所有文件名称
     */
    @JvmStatic
    fun getFileName(dir: Any, deep: Boolean): List<String> {
        val result = arrayListOf<String>()
        forEachFile(dir, deep) { result.add(it.name) }
        return result
    }

    /**
     * 获取文件夹内文件 (不存在时返回 null)
     * @param file 待获取文件路径
     * @return 对应文件
     */
    @JvmStatic
    fun getFileOrNull(file: String): File? {
        return File(plugin.dataFolder, file).let {
            if (!it.exists()) null
            else it
        }
    }

    /**
     * 获取文件夹内文件 (不存在时创建文件)
     * @param file 待获取文件路径
     * @return 对应文件
     */
    @JvmStatic
    fun getFileOrCreate(file: String): File {
        return File(plugin.dataFolder, file).createFile()
    }

    /**
     * 创建文件
     */
    @JvmStatic
    fun File.createFile(): File {
        if (!exists()) {
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            createNewFile()
        }
        return this
    }

    /**
     * 创建文件夹
     */
    @JvmStatic
    fun File.createDirectory(): File {
        if (!exists()) {
            mkdirs()
        }
        return this
    }

    /**
     * 释放资源文件(不覆盖)
     * @param resourcePath 文件路径
     */
    @JvmStatic
    fun JavaPlugin.releaseResource(resourcePath: String) {
        releaseResource(resourcePath, File(plugin.dataFolder, resourcePath))
    }

    /**
     * 释放资源文件(不覆盖)
     * @param resourcePath 文件路径
     * @param outFile 输出路径
     */
    @JvmStatic
    fun JavaPlugin.releaseResource(resourcePath: String, outFile: File) {
        getResource(resourcePath.replace('\\', '/'))?.use { input ->
            outFile.parentFile.createDirectory()
            if (!outFile.exists()) {
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun forEachFile(dir: Any, deep: Boolean, action: (File) -> Unit) {
        val parent = when (dir) {
            is String -> File(dir)
            is File -> dir
            else -> throw IllegalArgumentException()
        }

        fun innerForEach(file: File) {
            if (file.isFile) {
                action(file)
            } else if (deep && file.isDirectory && file.listFiles()?.isNotEmpty() == true) {
                file.listFiles()?.forEach { innerForEach(it) }
            }
        }
        parent.listFiles()?.forEach { innerForEach(it) }
    }
}