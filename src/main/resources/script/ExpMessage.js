/**
 * AkariLevel
 *
 * 经验获取提示脚本。
 *
 * @author 季楠
 * @since 2025/9/19 23:42
 */

function onPluginEnable() {
    onMemberExpChange();
}

// 经验来源。
var sources = ["MYTHICMOBS_DROP_EXP", "VANILLA_EXP_CHANGE"];

function onMemberExpChange() {
    new Listener(MemberExpChangeEvent.class)
        .setExecutor(
            function (event) {
                // 获取事件参数。
                var member = event.getMember();
                var expAmount = event.getExpAmount();
                var source = event.getSource();

                // 经验获取提示。
                var offlinePlayer = Bukkit.getOfflinePlayer(member);
                if (offlinePlayer.isOnline() && expAmount > 0 && sources.indexOf(source) !== -1) {
                    var player = offlinePlayer.getPlayer();
                    // player.sendMessage("§7Exp +§f" + expAmount + "§7.");
                }
            }
        ).register();
}