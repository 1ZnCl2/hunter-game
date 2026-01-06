package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.usecases.HunterTrackingUseCase
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLifecycleListener(
    private val trackingUseCase: HunterTrackingUseCase
) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        // 플레이어가 퇴장할 때 추적 상태 정리
        trackingUseCase.onPlayerQuit(player.uniqueId)
    }
}

