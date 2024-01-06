package com.github.cpjinan.plugin.playerlevel.internal.abstracts

import lombok.Getter
import lombok.NoArgsConstructor
import org.bukkit.event.HandlerList


/**
 * 用于快速实现同步事件
 *
 * @author 2000000
 * @since 2023/12/31
 */
@NoArgsConstructor
abstract class SyncEvent : BukkitEvent(false) {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @Getter
        private val handlerList = HandlerList()
    }
}
