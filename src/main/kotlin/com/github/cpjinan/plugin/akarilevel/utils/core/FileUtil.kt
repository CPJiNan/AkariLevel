package com.github.cpjinan.plugin.akarilevel.utils.core

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import java.io.File

object FileUtil {
    /**
     * 获取文件夹内所有文件
     * @param dir 待获取文件夹
     * @param deep 是否查找目标路径中文件夹内文件
     * @return 文件夹内所有文件
     */
    @JvmStatic
    fun getFile(dir: Any, deep: Boolean): ArrayList<File> {
        val result = arrayListOf<File>()
        traverseFile(dir, deep) { result.add(it) }
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
        traverseFile(dir, deep) { result.add(it.name) }
        return result
    }

    /**
     * 获取文件夹内文件 (不存在时返回null)
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
     * 读取文本文件
     * @param file 文本文件
     * @return 文件文本
     */
    @JvmStatic
    fun readText(file: File): String {
        return file.readText()
    }

    /**
     * 写入文本文件
     * @param file 文本文件
     * @param text 文件文本
     */
    @JvmStatic
    fun writeText(file: File, text: String) {
        file.writeText(text)
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