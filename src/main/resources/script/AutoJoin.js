/**
 * AkariLevel
 *
 * 自动加入等级组脚本。
 *
 * @author 季楠
 * @since 2025/8/18 17:28
 */

function onPluginEnable() {
    onPlayerJoin();
}

// 设置等级组列表。
var levelGroupNames = [];

function onPlayerJoin() {
    new Listener(PlayerJoinEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var uniqueId = event.player.getUniqueId().toString()

                // 自动加入等级组。
                levelGroupNames.forEach(
                    function (name) {
                        var levelGroup = LevelGroup.getLevelGroups()[name];
                        if (levelGroup !== null && !levelGroup.hasMember(uniqueId)) {
                            levelGroup.addMember(uniqueId, "AUTO_JOIN");
                        }
                    }
                );
            }
        ).register();
}