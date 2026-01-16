package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.components.PlayerState
import hunter.papermc.testplugin.services.HunterTrackingService
import hunter.papermc.testplugin.services.PlayerStateService
import hunter.papermc.testplugin.services.TeamService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HunterTrackingUsecase(
    private val trackingService: HunterTrackingService,
    private val playerStateService: PlayerStateService,
    private val teamService: TeamService
) {
    fun startTracking(hunter: Player): Boolean {
        // 헌터 상태 확인
        if (playerStateService.getState(hunter) != PlayerState.HUNTER) {
            hunter.sendMessage(Component.text("§c당신은 헌터가 아닙니다."))
            return false
        }

        // 팀 확인
        val hunterTeam = teamService.getTeam(hunter)
        if (hunterTeam == null) {
            hunter.sendMessage(Component.text("§c당신은 팀에 소속되어 있지 않습니다."))
            return false
        }

        /* if (trackingService.isTracking(hunter)) {
            hunter.sendMessage(Component.text("§7이미 추적 중입니다."))
            return false
        } */
        

        // 쿨타임 확인
        if (!trackingService.canStartTracking(hunter)) {
            val remainingMs = trackingService.getRemainingCooldownMillis(hunter)
            val remainingSec = (remainingMs / 1000L) + 1L
            hunter.sendMessage(
                Component.text("§c아직 나침반에 힘이 돌아오지 않았습니다! §7${remainingSec}초 후에 다시 사용할 수 있습니다.")
            )
            return false
        }

        // 추적 시작
        val started = trackingService.startTracking(hunter)
        if (started) {
            hunter.sendMessage(Component.text("§a추적을 시작합니다..."))
            // 즉시 한 번 업데이트
            updateTracking(hunter)
        }
        return started
    }

    fun stopTracking(hunter: Player) {
        if (!trackingService.isTracking(hunter)) {
            return
        }

        trackingService.stopTracking(hunter)
        trackingService.updateCompassTarget(hunter, null)
        hunter.sendMessage(Component.text("§7추적을 중지했습니다."))
    }

    fun toggleTracking(hunter: Player) {
        if (trackingService.isTracking(hunter)) {
            stopTracking(hunter)
        } else {
            startTracking(hunter)
        }
    }

    fun updateTracking(hunter: Player): Boolean {
        // 추적 중인지 확인
        if (!trackingService.isTracking(hunter)) {
            return false
        }

        // 10초 제한 시간 확인 (만료되면 자동 종료)
        if (trackingService.hasTrackingExpired(hunter)) {
            stopTracking(hunter)
            return false
        }

        // 헌터 상태 확인
        if (playerStateService.getState(hunter) != PlayerState.HUNTER) {
            stopTracking(hunter)
            return false
        }

        // 가장 가까운 적 찾기
        val target = trackingService.getNearestEnemy(hunter)

        if (target == null) {
            // 타겟이 없음
            trackingService.updateCompassTarget(hunter, null)
            hunter.sendActionBar(
                Component.text("§7추적할 대상이 없습니다!")
            )
            return false
        }

        // 나침반 업데이트
        trackingService.updateCompassTarget(hunter, target)

        // 거리 계산
        val distance = trackingService.getDistanceToTarget(hunter, target)
        val distanceText = if (distance != null) " §7(거리: ${distance}블록)" else ""

        // 액션바 메시지 표시
        hunter.sendActionBar(
            Component.text("§c추적 중: §f${distanceText}")
        )
        return true
    }

    fun updateAllTracking() {
        trackingService.getTrackingPlayers().forEach { hunter ->
            updateTracking(hunter)
        }
    }

    fun onPlayerNoLongerHunter(player: Player) {
        if (trackingService.isTracking(player)) {
            stopTracking(player)
        }
    }

    fun onPlayerQuit(uuid: java.util.UUID) {
        trackingService.stopTracking(uuid)
    }

    fun isTracking(player: Player): Boolean =
        trackingService.isTracking(player)
}

