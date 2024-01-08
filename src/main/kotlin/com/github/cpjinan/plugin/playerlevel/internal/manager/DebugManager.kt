package com.github.cpjinan.plugin.playerlevel.internal.manager

import taboolib.common.platform.function.info

/**
 * 调试管理器
 * @author CPJiNan
 * @date 2024/01/06
 */
object DebugManager {

    /**
     * 输出调试信息
     * @param [message] 消息
     */
    fun debugPrint(message: String?) {
        if (ConfigManager.options.getBoolean("debug")) info(message)
    }

    /**
     * 输出字符Logo
     */
    fun logoPrint() {
        info(" ______ _                       _                    _  ")
        info(" | ___ \\ |                     | |                  | | ")
        info(" | |_/ / | __ _ _   _  ___ _ __| |     _____   _____| | ")
        info(" |  __/| |/ _` | | | |/ _ \\ '__| |    / _ \\ \\ / / _ \\ | ")
        info(" | |   | | (_| | |_| |  __/ |  | |___|  __/\\ V /  __/ | ")
        info(" \\_|   |_|\\__,_|\\__, |\\___|_|  \\_____/\\___| \\_/ \\___|_| ")
        info("                 __/ |                                  ")
        info("                |___/                                   ")
    }

    /**
     * 空格替换
     * @return [String]
     */
    fun String.replaceSpace() : String = this.replace(ConfigManager.options.getString("space-replace")!!," ")

}