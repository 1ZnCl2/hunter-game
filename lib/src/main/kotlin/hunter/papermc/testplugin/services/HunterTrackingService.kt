package hunter.papermc.testplugin.services

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HunterTrackingService {
    private val trackingPlayers = mutableSetOf<Player>()
    private val trackedTargets = mutableMapOf<Player, Player>()

    fun isTracking(player: Player): Boolean =
        trackingPlayers.contains(player)

    fun startTracking(player: Player): Boolean {
        if (trackingPlayers.contains(player)) return false
        trackingPlayers.add(player)
        return true
    }

    fun getTrackingPlayers(): Set<Player> = trackingPlayers

    fun getRandomTarget(hunter: Player): Player? {
    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    val hunterTeam = scoreboard.getEntryTeam(hunter.name)

    fun getNearestEnemy(hunter: Player): Player? {
    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    val hunterTeam = scoreboard.getEntryTeam(hunter.name) ?: return null
    val hunterLoc = hunter.location
    val hunterWorld = hunter.world


    return Bukkit.getOnlinePlayers()
        .asSequence()
        .filter { it != hunter }
        .filter { it.world == hunterWorld } // ⭐ 월드 일치
        .filter { target ->
            val targetTeam = scoreboard.getEntryTeam(target.name)
            targetTeam != null && targetTeam != hunterTeam
        }
        .minByOrNull { target ->
            target.location.distanceSquared(hunter.location)
        }

    fun updateCompassTarget(hunter: Player, target: Player) {
        hunter.compassTarget = target.location
    }

    fun stopTracking(player: Player) {
        trackedTargets.remove(player)
    }
}
