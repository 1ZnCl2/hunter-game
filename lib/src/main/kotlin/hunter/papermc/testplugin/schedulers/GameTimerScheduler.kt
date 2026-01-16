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

        if (isCurrentlyRunning && !wasRunning) {
            wasRunning = true
            shownPlayers.clear()
        }

        if (!isCurrentlyRunning && wasRunning) {
            wasRunning = false
            removeBossBar()
            shownPlayers.clear()
            return
        }

        if (!isCurrentlyRunning) {
            return
        }

        val remainingSeconds = gameStateService.getRemainingSeconds()
        val progress = gameStateService.getProgress()
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60

        bossBar.progress(progress)
        bossBar.name(Component.text("§a남은 시간 : %02d:%02d:%02d".format(minutes / 60, minutes % 60, seconds)))

        Bukkit.getOnlinePlayers().forEach { player ->
            if (!shownPlayers.contains(player.uniqueId)) {
                shownPlayers.add(player.uniqueId)
                player.showBossBar(bossBar)
            }
        }

        val newColor = when {
            progress < 0.2f -> BossBar.Color.RED
            progress < 0.5f -> BossBar.Color.YELLOW
            else -> BossBar.Color.BLUE
        }
        if (bossBar.color() != newColor) {
            bossBar.color(newColor)
        }

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

    fun addPlayer(player: org.bukkit.entity.Player) {
        player.showBossBar(bossBar)
    }

    fun removePlayer(player: org.bukkit.entity.Player) {
        player.hideBossBar(bossBar)
    }
}
