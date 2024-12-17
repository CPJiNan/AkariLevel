package com.github.cpjinan.plugin.akarilevel.common

import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.utils.core.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.core.FileUtil
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText
import java.io.File

object PluginLanguage {
    val lang = console().asLangText("Language")
    fun saveDefaultResource() {
        plugin.saveDefaultResource(
            "level/Example_$lang.yml",
            File(FileUtil.dataFolder, "level/Example.yml")
        )
        plugin.saveDefaultResource(
            "settings_$lang.yml",
            File(FileUtil.dataFolder, "settings.yml")
        )
    }
}