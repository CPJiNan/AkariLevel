package com.github.cpjinan.plugin.akarilevel.utils.core

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import org.bukkit.Bukkit
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

object FileUtil {
    val dataFolder =
        Bukkit.getPluginManager().getPlugin(plugin.name)?.dataFolder ?: throw IllegalArgumentException(
            "Unable to find plugin data folder."
        )

    /**
     * 获取文件夹内所有文件
     * @param dir 待获取文件夹
     * @param deep 是否查找目标路径中文件夹内文件
     * @return 文件夹内所有文件
     * @author CPJiNan
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
     * @author CPJiNan
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
     * @author InkerXoe
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
     * @author InkerXoe
     */
    @JvmStatic
    fun getFileOrCreate(file: String): File {
        return File(plugin.dataFolder, file).createFile()
    }

    /**
     * 读取文本文件
     * @param file 文本文件
     * @return 文件文本
     * @author InkerXoe
     */
    @JvmStatic
    fun readText(file: File): String {
        return file.readText()
    }

    /**
     * 写入文本文件
     * @param file 文本文件
     * @param text 文件文本
     * @author InkerXoe
     */
    @JvmStatic
    fun writeText(file: File, text: String) {
        file.writeText(text)
    }

    /**
     * 创建文件
     * @author InkerXoe
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
     * @author InkerXoe
     */
    @JvmStatic
    fun File.createDirectory(): File {
        if (!exists()) {
            mkdirs()
        }
        return this
    }

    /**
     * 解析文件编码
     *
     * @param file 待解析文件
     * @return 编码类型
     */
    @JvmStatic
    fun charset(file: File): String {
        var charset = "GBK"
        val first3Bytes = ByteArray(3)
        try {
            var checked = false
            val bis = BufferedInputStream(FileInputStream(file))
            bis.mark(0)
            var read = bis.read(first3Bytes, 0, 3)
            if (read == -1) {
                bis.close()
                return charset
            } else if (first3Bytes[0] == 0xFF.toByte() && first3Bytes[1] == 0xFE.toByte()) {
                charset = "UTF-16LE"
                checked = true
            } else if (first3Bytes[0] == 0xFE.toByte() && first3Bytes[1] == 0xFF.toByte()) {
                charset = "UTF-16BE"
                checked = true
            } else if (first3Bytes[0] == 0xEF.toByte() && first3Bytes[1] == 0xBB.toByte() && first3Bytes[2] == 0xBF.toByte()) {
                charset = "UTF-8"
                checked = true
            }
            bis.reset()
            if (!checked) {
                while (bis.read().also { read = it } != -1) {
                    if (read >= 0xF0) break
                    if (read in 0x80..0xBF)
                        break
                    if (read in 0xC0..0xDF) {
                        read = bis.read()
                        if (read in 0x80..0xBF)
                            continue else break
                    } else if (read in 0xE0..0xEF) {
                        read = bis.read()
                        if (read in 0x80..0xBF) {
                            read = bis.read()
                            if (read in 0x80..0xBF) {
                                charset = "UTF-8"
                                break
                            } else break
                        } else break
                    }
                }
            }
            bis.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return charset
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