package hunter.papermc.testplugin.listeners

import hunter.papermc.testplugin.services.GameStateService
import hunter.papermc.testplugin.services.GameScoreService
import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.usecases.HunterTrackingUsecase
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.Sound

class KillListener(
    private val gameStateService: GameStateService,
    private val gameScoreService: GameScoreService,
    private val teamService: TeamService,
    private val trackingUsecase: HunterTrackingUsecase
) : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!gameStateService.isRunning()) return

        val deceased = event.entity
        val killer = deceased.killer ?: return

        if (!trackingUsecase.isTracking(killer)) return

        val killerTeam = teamService.getTeam(killer)
        if (killerTeam != null) {
            gameScoreService.addScore(killerTeam, 1)
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(
                deceased.location,
                Sound.ENTITY_PLAYER_LEVELUP,
                1.0f,
                1.0f
            )
        }

        Bukkit.broadcastMessage("§c[GAME] ${killer.name}님이 ${deceased.name}님을 처치했습니다!")

        trackingUsecase.stopTracking(deceased)
    }
}