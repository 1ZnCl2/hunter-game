package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.components.PlayerState
import hunter.papermc.testplugin.services.PlayerStateService
import hunter.papermc.testplugin.usecases.HunterTrackingUsecase
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class HunterUsingListener(
    private val playerStateService: PlayerStateService,
    private val trackingUsecase: HunterTrackingUsecase
) : Listener {

    @EventHandler
    fun onUse(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val meta = item.itemMeta ?: return

        if (item.type != Material.RECOVERY_COMPASS) return
        if (!meta.hasCustomModelData() || meta.customModelData != 1001) return
        if (event.action !in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) return

        // 헌터 상태 확인
        if (playerStateService.getState(player) != PlayerState.HUNTER) {
            player.sendTitle("", "§c당신은 술래가 아닙니다.", 10, 60, 10)
            return
        }

        // 추적 토글: 추적 중이면 중지, 아니면 시작
        event.isCancelled = true // 아이템 사용 이벤트 취소
        trackingUsecase.toggleTracking(player)
    }
}
