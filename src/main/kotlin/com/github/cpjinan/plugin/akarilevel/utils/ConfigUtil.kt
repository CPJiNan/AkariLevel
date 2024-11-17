package com.github.cpjinan.plugin.akarilevel.utils

import com.github.cpjinan.plugin.akarilevel.utils.FileUtil.createDirectory
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ConfigUtil {

    /**
     * 获取文件中所有ConfigurationSection
     * @return 文件中所有ConfigurationSection
     * @author CPJiNan
     */
    @JvmStatic
    fun File.getConfigSections(): HashMap<String, ConfigurationSection> {
        val map = HashMap<String, ConfigurationSection>()
        val config = YamlConfiguration.loadConfiguration(this)
        config.getKeys(false).forEach { key ->
            config.getConfigurationSection(key)?.let { map[key] = it }
        }
        return map
    }

    /**
     * 获取所有文件中所有ConfigurationSection
     * @return 文件中所有ConfigurationSection
     * @author CPJiNan
     */
    @JvmStatic
    fun ArrayList<File>.getConfigSections(): HashMap<String, ConfigurationSection> {
        val map = HashMap<String, ConfigurationSection>()
        for (file: File in this) {
            file.getConfigSections().forEach { (k, v) ->
                map[k] = v
            }
        }
        return map
    }

    /**
     * 保存默认文件(不覆盖)
     * @param resourcePath 文件路径
     * @author InkerXoe
     */
    @JvmStatic
    fun JavaPlugin.saveDefaultResource(resourcePath: String) {
        this.saveDefaultResource(resourcePath, File(this.dataFolder, resourcePath))
    }

    /**
     * 保存默认文件(不覆盖)
     * @param resourcePath 文件路径
     * @param outFile 输出路径
     * @author InkerXoe
     */
    @JvmStatic
    fun JavaPlugin.saveDefaultResource(resourcePath: String, outFile: File) {
        this.getResource(resourcePath.replace('\\', '/'))?.use { inputStream ->
            outFile.parentFile.createDirectory()
            if (!outFile.exists()) {
                FileOutputStream(outFile).use { fileOutputStream ->
                    var len: Int
                    val buf = ByteArray(1024)
                    while (inputStream.read(buf).also { len = it } > 0) {
                        (fileOutputStream as OutputStream).write(buf, 0, len)
                    }
                }
            }
        }
    }

    /**
     * 将多个 YamlConfiguration 合并
     * @return 合并后的 YamlConfiguration
     * @author CPJiNan
     */
    @JvmStatic
    fun getMergedConfig(sections: HashMap<String, ConfigurationSection>): YamlConfiguration {
        val newConfig = YamlConfiguration()
        sections.forEach { (key, section) ->
            section.getValues(true).forEach { (subKey, value) ->
                newConfig.set("$key.$subKey", value)
            }
        }
        return newConfig
    }
}