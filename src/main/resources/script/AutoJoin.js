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
                var levelGroupNames = new java.util.ArrayList();
                // levelGroupNames.add("Example");

                // 自动加入等级组。
                levelGroupNames.forEach(
                    function (name) {
                        var levelGroup = LevelGroup.getLevelGroups()[name]
                        if (levelGroup !== null && !levelGroup.hasMember(playerName)) {
                            levelGroup.addMember(playerName, "AUTO_JOIN")
                        }
                    }
                );
            }
        ).register();
}