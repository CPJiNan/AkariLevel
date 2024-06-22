package com.github.cpjinan.plugin.akarilevel.internal.manager

import com.github.cpjinan.plugin.akarilevel.AkariLevel
import com.github.cpjinan.plugin.akarilevel.AkariLevel.plugin
import com.github.cpjinan.plugin.akarilevel.utils.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akarilevel.utils.FileUtil
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText
import java.io.File

object LanguageManager {
    val lang = console().asLangText("Language")

    fun saveDefaultResource() {
        plugin.saveDefaultResource("level/Example_$lang.yml", File(FileUtil.dataFolder, "level/Example.yml"))
    }
}