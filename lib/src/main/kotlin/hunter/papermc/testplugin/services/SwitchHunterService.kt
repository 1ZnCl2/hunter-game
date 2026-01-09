package hunter.papermc.testplugin.services

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SwitchHunterService {
    fun switchHunter(hunterPlayer: Player) {
        Bukkit.getOnlinePlayers().forEach { p ->
            p.sendTitle(
                "§7술래 탄생!",
                "§f${hunterPlayer.name}를 피하세요!",
                10, 60, 10
            )

            val inv = p.inventory
            for (i in 0 until inv.size) {
                val item = inv.getItem(i) ?: continue
                val m = item.itemMeta ?: continue

                if (m.hasCustomModelData() && m.customModelData == 1001 && p != hunterPlayer) {
                    inv.clear(i)
                    p.sendMessage("§7[시스템] 다른 플레이어가 술래 아이템을 제작해 당신의 나침반이 사라집니다...")
                }
            }
        }
    }
}
