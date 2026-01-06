package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.usecases.HunterTrackingUsecase
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLifecycleListener(
    private val trackingUsecase: HunterTrackingUsecase
) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        // 플레이어가 퇴장할 때 추적 상태 정리
        trackingUsecase.onPlayerQuit(player.uniqueId)
    }
}

