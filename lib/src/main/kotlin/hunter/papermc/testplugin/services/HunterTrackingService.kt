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

    private val trackingStartTimes = mutableMapOf<UUID, Long>()

    private val cooldownUntil = mutableMapOf<UUID, Long>()

    private val TRACKING_DURATION_MS = 10_000L // 10초
    private val COOLDOWN_MS = 50_000L         // 50초

    fun isTracking(player: Player): Boolean =
        trackingPlayers.contains(player.uniqueId)

    fun isTracking(uuid: UUID): Boolean =
        trackingPlayers.contains(uuid)

    fun startTracking(player: Player): Boolean {
        val uuid = player.uniqueId
        if (trackingPlayers.contains(uuid)) return false

        trackingPlayers.add(uuid)
        trackingStartTimes[uuid] = System.currentTimeMillis()
        return true
    }

    fun stopTracking(player: Player) {
        val uuid = player.uniqueId
        trackingPlayers.remove(uuid)
        trackingStartTimes.remove(uuid)
        // 추적이 종료될 때마다 50초 쿨타임 부여
        cooldownUntil[uuid] = System.currentTimeMillis() + COOLDOWN_MS
    }

    fun stopTracking(uuid: UUID) {
        trackingPlayers.remove(uuid)
        trackingStartTimes.remove(uuid)
        cooldownUntil[uuid] = System.currentTimeMillis() + COOLDOWN_MS
    }

    fun stopAllTracking() {
        trackingPlayers.clear()
        trackingStartTimes.clear()
    }

    fun getTrackingPlayerUUIDs(): Set<UUID> = trackingPlayers.toSet()

    fun getTrackingPlayers(): Set<Player> =
        trackingPlayers.mapNotNull { uuid -> Bukkit.getPlayer(uuid) }.toSet()

    /**
     * 현재 플레이어가 추적을 새로 시작할 수 있는지 여부
     */
    fun canStartTracking(player: Player): Boolean =
        getRemainingCooldownMillis(player) <= 0

    /**
     * 쿨타임이 얼마나 남았는지 (ms). 없으면 0.
     */
    fun getRemainingCooldownMillis(player: Player): Long {
        val uuid = player.uniqueId
        val until = cooldownUntil[uuid] ?: return 0L
        val remaining = until - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0L
    }

    /**
     * 10초 추적 시간이 지났는지 여부
     */
    fun hasTrackingExpired(player: Player): Boolean {
        val uuid = player.uniqueId
        val start = trackingStartTimes[uuid] ?: return false
        return System.currentTimeMillis() - start >= TRACKING_DURATION_MS
    }

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
