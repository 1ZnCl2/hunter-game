package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.services.GameStateService
import hunter.papermc.testplugin.usecases.GameControlUsecase
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class GameTimerScheduler(
    private val gameStateService: GameStateService,
    private val gameControlUsecase: GameControlUsecase
) : BukkitRunnable() {
    private val shownPlayers = mutableSetOf<UUID>()
    private var wasRunning = false

    private val bossBar = BossBar.bossBar(
        Component.text("§a게임 진행 중"),
        1.0f,
        BossBar.Color.GREEN,
        BossBar.Overlay.PROGRESS
    )

    override fun run() {
        val isCurrentlyRunning = gameStateService.isRunning()

        // 게임이 시작된 경우
        if (isCurrentlyRunning && !wasRunning) {
            wasRunning = true
            shownPlayers.clear()
        }

        // 게임이 종료된 경우
        if (!isCurrentlyRunning && wasRunning) {
            wasRunning = false
            removeBossBar()
            shownPlayers.clear()
            return
        }

        // 게임이 실행 중이 아니면 아무것도 하지 않음
        if (!isCurrentlyRunning) {
            return
        }

        // 게임 실행 중: 보스바 업데이트
        val remainingSeconds = gameStateService.getRemainingSeconds()
        val progress = gameStateService.getProgress()
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60

        bossBar.progress(progress)
        bossBar.name(Component.text("§a남은 시간 : %02d:%02d".format(minutes, seconds)))

        // 온라인 플레이어에게 보스바 표시
        Bukkit.getOnlinePlayers().forEach { player ->
            if (!shownPlayers.contains(player.uniqueId)) {
                shownPlayers.add(player.uniqueId)
                player.showBossBar(bossBar)
            }
        }

        // 색상 변경
        val newColor = when {
            progress < 0.2f -> BossBar.Color.RED
            progress < 0.5f -> BossBar.Color.YELLOW
            else -> BossBar.Color.BLUE
        }
        if (bossBar.color() != newColor) {
            bossBar.color(newColor)
        }

        // 시간 경고 메시지
        when (remainingSeconds) {
            600L -> Bukkit.broadcastMessage("§e게임 종료까지 10분 남았습니다!")
            300L -> Bukkit.broadcastMessage("§e게임 종료까지 5분 남았습니다!")
            60L -> Bukkit.broadcastMessage("§c게임 종료까지 1분 남았습니다!")
            10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L -> {
                Bukkit.broadcastMessage("§c§l${remainingSeconds}초...")
            }
        }

        // 시간 만료 시 게임 자동 종료
        if (remainingSeconds <= 0) {
            gameControlUsecase.endGame()
            removeBossBar()
            wasRunning = false
            shownPlayers.clear()
        }
    }

    private fun removeBossBar() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.hideBossBar(bossBar)
        }
    }
}
