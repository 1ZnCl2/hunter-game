package hunter.papermc.testplugin.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.entity.Player

class HunterCraftListener : Listener {

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val meta = event.recipe.result.itemMeta ?: return
        val hunter = event.whoClicked as? Player ?: return

        if (!meta.hasCustomModelData() || meta.customModelData != 1001) return

        Bukkit.getOnlinePlayers().forEach { p ->
            p.sendTitle(
                "§7새로운 술래가 탄생했습니다!",
                "§f${hunter.name}를 피하세요!",
                10, 60, 10
            )
        }

        
    }
}
