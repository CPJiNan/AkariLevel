package top.cpjinan.akarilevel.script

import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.plugin.Plugin
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.platform.BukkitPlugin

/**
 * AkariLevel
 * top.cpjinan.akarilevel.script
 *
 * 脚本命令。
 *
 * @author NeigeItems, 季楠
 * @since 2025/10/25 20:50
 */
class ScriptCommand(val name: String) {
    companion object {
        private val commandMap: SimpleCommandMap by lazy {
            Bukkit.getPluginManager().getProperty<SimpleCommandMap>("commandMap")!!
        }

        private val knownCommands: MutableMap<String, Command> by lazy {
            commandMap.getProperty<MutableMap<String, Command>>("knownCommands")!!
        }

        /**
         * 注册命令。
         *
         * @param name          命令名。
         * @param executor      命令执行器。
         * @return [PluginCommand] 对象。
         */
        @JvmStatic
        fun registerCommand(
            name: String,
            executor: CommandExecutor
        ): PluginCommand {
            return registerCommand(
                name = name,
                executor = executor
            )
        }

        /**
         * 注册命令。
         *
         * @param name          命令名。
         * @param nameSpace     命名空间。
         * @param executor      命令执行器。
         * @return [PluginCommand] 对象。
         */
        @JvmStatic
        fun registerCommand(
            name: String,
            nameSpace: String = name,
            executor: CommandExecutor
        ): PluginCommand {
            return registerCommand(
                name = name,
                nameSpace = nameSpace,
                executor = executor
            )
        }

        /**
         * 注册命令。
         *
         * @param name          命令名。
         * @param nameSpace     命名空间。
         * @param plugin        注册命令的插件。
         * @param executor      命令执行器。
         * @return [PluginCommand] 对象。
         */
        @JvmStatic
        fun registerCommand(
            name: String,
            nameSpace: String = name,
            plugin: Plugin = BukkitPlugin.getInstance(),
            executor: CommandExecutor
        ): PluginCommand {
            val command = PluginCommand::class.java.invokeConstructor(name.lowercase(), plugin)
            command.executor = executor
            commandMap.register(nameSpace, command)
            return command
        }

        /**
         * 取消注册命令。
         *
         * @param name 待取消注册的命令名。
         */
        @JvmStatic
        fun unregisterCommand(name: String?) {
            name?.let {
                knownCommands.remove(it)
            }
        }
    }

    private var command: PluginCommand? = null
    private var nameSpace: String = name
    private var executor: CommandExecutor = CommandExecutor { _, _, _, _ -> false }
    private var tabCompleter: TabCompleter? = null
    private var plugin: Plugin = BukkitPlugin.getInstance()
    private var aliases: List<String> = emptyList()
    private var description: String = ""
    private var usage: String = ""
    private var permission: String? = null
    private var permissionMessage: String? = null

    /**
     * 设置注册命令的插件。
     *
     * @param plugin 插件。
     * @return 修改后的 ScriptCommand 本身。
     */
    fun setPlugin(plugin: Plugin): ScriptCommand {
        this.plugin = plugin
        return this
    }

    /**
     * 设置命令命名空间。
     *
     * @param nameSpace 命名空间。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setNameSpace(nameSpace: String): ScriptCommand {
        this.nameSpace = nameSpace
        return this
    }

    /**
     * 设置命令执行器。
     *
     * @param executor 命令执行器。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setExecutor(executor: CommandExecutor): ScriptCommand {
        this.executor = executor
        return this
    }

    /**
     * 设置命令补全器。
     *
     * @param tabCompleter 命令补全器。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setTabCompleter(tabCompleter: TabCompleter): ScriptCommand {
        this.tabCompleter = tabCompleter
        return this
    }

    /**
     * 设置命令别名。
     *
     * @param aliases 命令别名。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setAliases(aliases: List<String>): ScriptCommand {
        this.aliases = aliases
        return this
    }

    /**
     * 设置命令描述。
     *
     * @param description 命令描述。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setDescription(description: String): ScriptCommand {
        this.description = description
        return this
    }

    /**
     * 设置命令用法。
     *
     * @param usage 命令用法。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setUsage(usage: String): ScriptCommand {
        this.usage = usage
        return this
    }

    /**
     * 设置命令所需权限。
     *
     * @param permission 命令所需权限。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setPermission(permission: String): ScriptCommand {
        this.permission = permission
        return this
    }

    /**
     * 设置无权限提示消息。
     *
     * @param permissionMessage 无权限提示消息。
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun setPermissionMessage(permissionMessage: String): ScriptCommand {
        this.permissionMessage = permissionMessage
        return this
    }

    /**
     * 注册命令。
     *
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun register(): ScriptCommand {
        unregister()
        command = registerCommand(
            name,
            nameSpace,
            plugin,
            executor
        )
        command?.let {
            if (tabCompleter != null) it.tabCompleter = tabCompleter
            if (aliases.isNotEmpty()) it.aliases = aliases
            if (description.isNotEmpty()) it.description = description
            if (usage.isNotEmpty()) it.usage = usage
            if (permission != null) it.permission = permission
            if (permissionMessage != null) it.permissionMessage = permissionMessage
        }
        ScriptHandler.commands.add(this)
        return this
    }

    /**
     * 取消注册命令。
     *
     * @return 修改后的 [ScriptCommand] 本身。
     */
    fun unregister(): ScriptCommand {
        command?.let {
            unregisterCommand(it.name)
            unregisterCommand("${nameSpace}:${it.name}")
            it.aliases.forEach { alias ->
                unregisterCommand(alias)
                unregisterCommand("${nameSpace}:${alias}")
            }
        }
        return this
    }
}