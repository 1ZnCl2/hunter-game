package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.services.PlayerStateService
import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.services.PlayerState
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SwitchHunterUseCase(
    private val teamService: TeamService,
    private val playerStateService: PlayerStateService
) {
    fun execute(hunter: Player) {
        val hunterTeam = teamService.getTeam(hunter)
            ?: return

        playerStateService.setState(hunter, PlayerState.HUNTER)

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
    }
}
