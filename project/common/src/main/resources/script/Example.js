function pluginEnable() {
    onPluginPreReload()
}

function onPluginPreReload() {
    new Listener(Packages.com.github.cpjinan.plugin.akarilevel.event.AkariLevelReloadEvent.Pre.class)
        .setExecutor(
            function (event) {
                print("测试");
            }
        ).register();
}