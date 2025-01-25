function pluginEnable() {
    onPluginPreReload()
}

function onPluginPreReload() {
    new Listener(Packages.org.bukkit.event.player.PlayerJoinEvent.class)
        .setExecutor(
            function (event) {
                print("测试");
            }
        ).register();
}