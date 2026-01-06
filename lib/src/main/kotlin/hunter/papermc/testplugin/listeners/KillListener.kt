package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.services.GameStateService
import hunter.papermc.testplugin.usecases.HunterTrackingUseCase
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.Sound

class KillListener(
    private val gameStateService: GameStateService,
    private val trackingUseCase: HunterTrackingUseCase
) : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!gameStateService.isRunning()) return

        val deceased = event.entity
        val killer = deceased.killer ?: return

        // 킬 소리 재생
        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(
                deceased.location,
                Sound.ENTITY_PLAYER_LEVELUP,
                1.0f,
                1.0f
            )
        }

        // 킬 메시지 브로드캐스트
        Bukkit.broadcastMessage("§c[GAME] ${killer.name}님이 ${deceased.name}님을 처치했습니다!")

        // 추적 초기화 (사망한 플레이어의 추적 중지)
        trackingUseCase.stopTracking(deceased)
    }
}