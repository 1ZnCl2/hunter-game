package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.services.HunterTrackingService
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class HunterUsingListener(
    private val service: HunterTrackingService
) : Listener {

    @EventHandler
    fun onUse(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val meta = item.itemMeta ?: return

        if (item.type != Material.RECOVERY_COMPASS) return
        if (!meta.hasCustomModelData() || meta.customModelData != 1001) return
        if (event.action !in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) return

        if (service.startTracking(player)) {
            player.sendTitle("§a상대를 추적합니다...")
        } else {
            player.sendTitle("§c이미 추적이 활성화되어 있습니다.")
        }
    }
}
