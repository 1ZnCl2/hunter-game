package hunter.papermc.testplugin.services

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HunterTrackingService {
    private val trackingPlayers = mutableSetOf<Player>()
    private val trackedTargets = mutableMapOf<Player, Player>()
    private val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    val online = Bukkit.getOnlinePlayers().toList()
    private val teamMembers = mutableMapOf<TeamType, MutableSet<UUID>>()

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
    teamService: TeamService
    ): Player? {

    val hunterTeam = teamService.getTeam(hunter) ?: return null
    val enemyTeams = TeamType.values().filter { it != hunterTeam }

    val hunterLoc = hunter.location
    var nearest: Player? = null
    var minDist = Double.MAX_VALUE

    for (team in enemyTeams) {
        for (uuid in teamService.getTeamMembers(team)) {
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

    }

    fun updateCompassTarget(hunter: Player, target: Player) {
        hunter.compassTarget = target.location
    }

    fun stopTracking(player: Player) {
        trackingPlayers.remove(player)
        trackedTargets.remove(player)
    }
}
