/**
 * AkariLevel
 *
 * 自动加入等级组脚本。
 *
 * @author 季楠
 * @since 2025/8/18 17:28
 */

function onPluginEnable() {
    onPlayerJoin()
}

function onPlayerJoin() {
    new Listener(PlayerJoinEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var playerName = event.getPlayer().getName();

                // 设置等级组列表。
                var levelGroups = new java.util.ArrayList();
                // levelGroups.add("Example");

                // 自动加入等级组。
                levelGroups.forEach(
                    function (name) {
                        var group = LevelGroup.getLevelGroups()[name]
                        if (group !== null && !group.hasMember(playerName)) {
                            group.addMember(playerName, "AUTO_JOIN")
                        }
                    }
                );
            }
        ).register();
}