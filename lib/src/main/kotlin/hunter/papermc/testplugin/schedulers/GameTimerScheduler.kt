package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.services.GameStateService
import hunter.papermc.testplugin.usecases.GameControlUsecase
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class GameTimerScheduler(
    private val gameStateService: GameStateService,
    private val gameControlUsecase: GameControlUsecase
) : BukkitRunnable() {

    private val bossBar = BossBar.bossBar(
        Component.text("§a게임 진행 중"),
        1.0f,
        BossBar.Color.GREEN,
        BossBar.Overlay.PROGRESS
    )

    override fun run() {
        if (!gameStateService.isRunning()) {
            cancel()
            return
        }

        val remainingSeconds = gameStateService.getRemainingSeconds()
        val progress = gameStateService.getProgress()
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60

        // 보스 바 업데이트
        bossBar.progress(progress.toDouble())
        bossBar.name(Component.text("§a남은 시간 : %02d:%02d".format(minutes, seconds)))

        Bukkit.getOnlinePlayers().forEach { player ->
            if (!player.showBossBar(bossBar)) {
                player.showBossBar(bossBar)
            }
        }

        val newColor = when {
            progress < 0.2f -> BossBar.Color.RED
            progress < 0.5f -> BossBar.Color.YELLOW
            else -> BossBar.Color.WHITE
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

        if (remainingSeconds <= 0) {
            gameControlUsecase.endGame()
            removeBossBar()
            cancel()
        }
    }

    fun removeBossBar() {
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
