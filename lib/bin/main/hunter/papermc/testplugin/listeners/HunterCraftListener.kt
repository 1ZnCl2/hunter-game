package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.services.HunterTrackingService
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.entity.Player
import net.kyori.adventure.text.Component

class HunterCraftListener(
    private val trackingService: HunterTrackingService
) : Listener {

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val meta = event.recipe.result.itemMeta ?: return
        val hunter = event.whoClicked as? Player ?: return

        if (!meta.hasCustomModelData() || meta.customModelData != 1001) return
        
        trackingService.startTracking(hunter)

        Bukkit.getOnlinePlayers().forEach { p ->
            p.sendTitle(
                "§7${hunter.name}님이 술래가 되었습니다!",
                "§f도주하세요!",
                10, 60, 10
            )
        }
    }
}
