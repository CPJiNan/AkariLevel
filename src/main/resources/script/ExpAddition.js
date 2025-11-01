/**
 * AkariLevel
 *
 * 经验加成属性脚本。
 *
 * @author 季楠
 * @since 2025/8/24 10:27
 */

function onPluginEnable() {
    onMemberExpChange();
}

// 导入类。
var PlaceholderAPI = Packages.me.clip.placeholderapi.PlaceholderAPI;

// 启用经验加成。
var enable = false;
// 经验来源。
var sources = ["MYTHICMOBS_DROP_EXP"];
// 经验加成属性变量。
var placeholder = "%ap_exp_addition:max%";

function onMemberExpChange() {
    new Listener(MemberExpChangeEvent.class)
        .setExecutor(
            function (event) {
                if (!enable) return;

                // 获取事件参数。
                var member = event.getMember();
                var expAmount = event.getExpAmount();
                var source = event.getSource();

                // 经验加成。
                var offlinePlayer = Bukkit.getOfflinePlayer(member);
                if (offlinePlayer.isOnline() && expAmount > 0 && sources.indexOf(source) !== -1) {
                    var player = offlinePlayer.getPlayer();
                    var expAddition = 1 + PlaceholderAPI.setPlaceholders(player, placeholder) / 100;
                    event.setExpAmount(Math.floor(expAmount * expAddition));
                }
            }
        ).register();
}