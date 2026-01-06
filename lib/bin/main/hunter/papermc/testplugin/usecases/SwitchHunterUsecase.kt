package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.components.TeamType
import hunter.papermc.testplugin.components.PlayerState
import hunter.papermc.testplugin.services.PlayerStateService
import hunter.papermc.testplugin.services.SwitchHunterService

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SwitchHunterUseCase(
    private val teamService: TeamService,
    private val playerStateService: PlayerStateService,
    private val switchHunterService: SwitchHunterService,
    private val trackingUseCase: HunterTrackingUseCase? = null
) {
    fun execute(hunter: Player) {
        val hunterTeam = teamService.getTeam(hunter)
            ?: return

        // 기존 헌터들의 추적 중지
        Bukkit.getOnlinePlayers().forEach { player ->
            if (playerStateService.getState(player) == PlayerState.HUNTER) {
                trackingUseCase?.onPlayerNoLongerHunter(player)
            }
        }

        // 모든 상태 리셋
        playerStateService.resetAllStates()
        
        // 새 헌터 설정
        playerStateService.setState(hunter, PlayerState.HUNTER)
        switchHunterService.switchHunter(hunter)

        // 다른 플레이어들의 상태 설정
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player == hunter) return@forEach

            val team = teamService.getTeam(player)
                ?: return@forEach

            if (team == hunterTeam) {
                playerStateService.setState(player, PlayerState.NORMAL)
            } else {
                playerStateService.setState(player, PlayerState.PREY)
            }
        }

        // 새 헌터의 추적 자동 시작
        trackingUseCase?.onPlayerBecameHunter(hunter)
    }
}
