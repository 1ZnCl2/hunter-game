package hunter.papermc.testplugin.service

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HunterTrackingService {
    private val trackingPlayers = mutableSetOf<Player>()

    fun isTracking(player: Player): Boolean =
        trackingPlayers.contains(player)

    fun startTracking(player: Player): Boolean {
        if (trackingPlayers.contains(player)) return false
        trackingPlayers.add(player)
        return true
    }

    fun getTrackingPlayers(): Set<Player> = trackingPlayers

    fun getRandomTarget(exclude: Player): Player? {
        val targets = Bukkit.getOnlinePlayers().filter { it != exclude }
        return targets.randomOrNull()
    }
}
