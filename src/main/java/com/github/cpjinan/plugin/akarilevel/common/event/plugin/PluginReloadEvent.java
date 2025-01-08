package com.github.cpjinan.plugin.akarilevel.common.event.plugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class PluginReloadEvent {
    /**
     * 插件重载前触发
     */
    public static class Pre extends Event {
        private static final HandlerList handlers = new HandlerList();

        public Pre() {
        }


        @NotNull
        public static HandlerList getHandlerList() {
            return handlers;
        }

        @Override
        @NotNull
        public HandlerList getHandlers() {
            return handlers;
        }

        public boolean call() {
            Bukkit.getPluginManager().callEvent(this);
            return true;
        }
    }

    /**
     * 插件重载后触发 (不可取消)
     */
    public static class Post extends Event {
        private static final HandlerList handlers = new HandlerList();

        public Post() {
        }

        @NotNull
        public static HandlerList getHandlerList() {
            return handlers;
        }

        @Override
        @NotNull
        public HandlerList getHandlers() {
            return handlers;
        }

        public boolean call() {
            Bukkit.getPluginManager().callEvent(this);
            return true;
        }
    }
}