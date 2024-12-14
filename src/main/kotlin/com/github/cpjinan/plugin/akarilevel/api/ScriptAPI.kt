package com.github.cpjinan.plugin.akarilevel.api

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.getConfigSections
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.io.File

object ScriptAPI {
    private var scriptFiles: ArrayList<File> = arrayListOf()
    private var scriptSections: HashMap<String, ConfigurationSection> = hashMapOf()
    private var scriptNames: ArrayList<String> = arrayListOf()
    private var scriptConfig: YamlConfiguration = YamlConfiguration()

    init {
        reloadScript()
    }

    /**
     * 重载脚本配置文件
     * @author CPJiNan
     */
    fun reloadScript() {
        scriptFiles = FileUtil.getFile(File(FileUtil.dataFolder, "script"), true)
            .filter { it.name.endsWith(".yml") }.toCollection(ArrayList())
        scriptSections = scriptFiles.getConfigSections()
        scriptNames = scriptSections.map { it.key }.toCollection(ArrayList())
        scriptConfig = ConfigUtil.getMergedConfig(scriptSections)
    }

    /**
     * 获取所有脚本的配置文件
     * @return 脚本配置文件列表
     * @author CPJiNan
     */
    fun getScriptFiles(): ArrayList<File> = scriptFiles

    /**
     * 获取所有脚本的配置节点
     * @return 脚本配置节点列表 (由 脚本ID 及其 配置节点 组成)
     * @author CPJiNan
     */
    fun getScriptSections(): HashMap<String, ConfigurationSection> = scriptSections

    /**
     * 获取所有脚本的名称
     * @return 脚本名称列表
     * @author CPJiNan
     */
    fun getScriptNames(): ArrayList<String> =
        scriptNames.filter { scriptSections[it]?.getBoolean("Hide") != true }.toCollection(ArrayList())

    /**
     * 获取所有脚本配置合并后的新配置
     * @return 脚本配置
     * @author CPJiNan
     */
    fun getScriptConfig(): YamlConfiguration = scriptConfig

    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        plugin.saveDefaultResource(
            "script/Example.yml"
        )
        reloadScript()
    }
}