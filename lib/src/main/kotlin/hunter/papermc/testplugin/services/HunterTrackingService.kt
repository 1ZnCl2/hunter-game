package hunter.papermc.testplugin.services

import hunter.papermc.testplugin.components.TeamType
import hunter.papermc.testplugin.services.TeamService

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class HunterTrackingService(
    private val teamService: TeamService
) {
    private val trackingPlayers = mutableSetOf<Player>()

    fun isTracking(player: Player): Boolean =
        trackingPlayers.contains(player)

    fun startTracking(player: Player): Boolean {
        if (trackingPlayers.contains(player)) return false
        trackingPlayers.add(player)
        return true
    }

    fun getTrackingPlayers(): Set<Player> = trackingPlayers

    fun getNearestEnemy(
    hunter: Player,
    ): Player? {

    val hunterTeam = teamService.getTeam(hunter) ?: return null
    val enemyTeams = TeamType.values().filter { it != hunterTeam }

    val hunterLoc = hunter.location
    var nearest: Player? = null
    var minDist = Double.MAX_VALUE

    for (team in enemyTeams) {
        for (uuid: UUID in teamService.getTeamMembers(team)) {
            val target = Bukkit.getPlayer(uuid) ?: continue
            if (target.world != hunter.world) continue

            val dist = target.location.distanceSquared(hunterLoc)
            if (dist < minDist) {
                    minDist = dist
                nearest = target
            }
        }
    }
        return nearest
    }


    fun updateCompassTarget(hunter: Player, target: Player) {
        hunter.compassTarget = target.location
    }

    fun stopTracking(player: Player) {
        trackingPlayers.remove(player)
    }
}
