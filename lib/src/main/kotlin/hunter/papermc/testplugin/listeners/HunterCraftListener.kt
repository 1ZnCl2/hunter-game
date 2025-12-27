package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.usecases.SwitchHunterUseCase
import hunter.papermc.testplugin.services.HunterTrackingService

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.entity.Player
import net.kyori.adventure.text.Component

class HunterCraftListener(
    private val switchHunterUseCase: SwitchHunterUseCase
) : Listener {

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val meta = event.recipe.result.itemMeta ?: return
        val hunter = event.whoClicked as? Player ?: return

        if (!meta.hasCustomModelData() || meta.customModelData != 1001) return
        
        switchHunterUseCase.execute(hunter)

        Bukkit.getOnlinePlayers().forEach { p ->
            p.sendTitle(
                "§7술래 탄생!",
                "§f${hunter.name}님이 술래가 되었습니다!",
                10, 60, 10
            )
        }
    }
}
