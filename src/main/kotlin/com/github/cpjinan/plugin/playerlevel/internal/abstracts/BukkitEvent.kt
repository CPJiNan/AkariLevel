package com.github.cpjinan.plugin.playerlevel.internal.abstracts

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

@Getter
@Setter
@NoArgsConstructor
abstract class BukkitEvent(async: Boolean) : Event(async), Cancellable {
    private val cancelled = false
}