package hunter.papermc.testplugin.services

import hunter.papermc.testplugin.components.TeamType
import hunter.papermc.testplugin.services.TeamService

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class HunterTrackingService(
    private val teamService: TeamService
) {
    private val trackingPlayers = mutableSetOf<UUID>()

    fun isTracking(player: Player): Boolean =
        trackingPlayers.contains(player.uniqueId)

    fun isTracking(uuid: UUID): Boolean =
        trackingPlayers.contains(uuid)

        fun startTracking(player: Player): Boolean {
        if (trackingPlayers.contains(player.uniqueId)) return false
        trackingPlayers.add(player.uniqueId)
        return true
    }


    fun stopTracking(player: Player) {
        trackingPlayers.remove(player.uniqueId)
    }

    fun stopTracking(uuid: UUID) {
        trackingPlayers.remove(uuid)
    }

    fun stopAllTracking() {
        trackingPlayers.clear()
    }

    fun getTrackingPlayerUUIDs(): Set<UUID> = trackingPlayers.toSet()

    fun getTrackingPlayers(): Set<Player> =
        trackingPlayers.mapNotNull { uuid -> Bukkit.getPlayer(uuid) }.toSet()

    fun getNearestEnemy(hunter: Player): Player? {
        val hunterTeam = teamService.getTeam(hunter) ?: return null
        val enemyTeams = TeamType.values().filter { it != hunterTeam }

        val hunterLoc = hunter.location
        var nearest: Player? = null
        var minDist = Double.MAX_VALUE

        for (team in enemyTeams) {
            for (uuid: UUID in teamService.getTeamMembers(team)) {
                val target = Bukkit.getPlayer(uuid) ?: continue
                // 같은 월드에 있는지 확인
                if (target.world != hunter.world) continue
                // 플레이어가 온라인이고 살아있는지 확인
                if (!target.isOnline || target.isDead) continue

                val dist = target.location.distanceSquared(hunterLoc)
                if (dist < minDist) {
                    minDist = dist
                    nearest = target
                }
            }
        }
        return nearest
    }

    fun getDistanceToTarget(hunter: Player, target: Player?): Int? {
        if (target == null) return null
        if (hunter.world != target.world) return null
        return hunter.location.distance(target.location).toInt()
    }

    fun updateCompassTarget(hunter: Player, target: Player?) {
        if (target == null) {
            // 타겟이 없으면 나침반을 원래 스폰 위치로 리셋
            hunter.compassTarget = hunter.world.spawnLocation
            return
        }
        hunter.compassTarget = target.location
    }
}
