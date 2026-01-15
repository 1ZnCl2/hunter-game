package hunter.papermc.testplugin.services

import hunter.papermc.testplugin.components.GamePhase
import org.bukkit.Bukkit

class GameStateService {
    var phase: GamePhase = GamePhase.WAITING
        private set

    private var gameStartTime: Long = 0
    private var timerTaskId: Int = -1

    companion object {
        const val GAME_DURATION_SECONDS = 2 * 60 * 60
        const val GAME_DURATION_TICKS = GAME_DURATION_SECONDS * 20
    }

    fun startGame() {
        if (phase != GamePhase.WAITING) return
        phase = GamePhase.RUNNING
        gameStartTime = System.currentTimeMillis()

        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendTitle(
                "§a게임 시작!",
                "",
                10, 60, 10
            )
        }
    }

    fun endGame() {
        if (phase != GamePhase.RUNNING) return
        phase = GamePhase.END

        if (timerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(timerTaskId)
            timerTaskId = -1
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendTitle(
                "§c게임 종료!",
                "§7결과를 집계합니다...",
                10, 60, 10
            )
        }
    }

    fun pauseGame() {
        if (phase != GamePhase.RUNNING) return
        phase = GamePhase.WAITING

        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendTitle(
                "§e게임 중지!",
                "§7지금부터 하는 킬은 점수로 들어가지 않습니다.",
                10, 60, 10
            )
        }
    }

    fun resetGame() {
        phase = GamePhase.WAITING
        gameStartTime = 0
        if (timerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(timerTaskId)
            timerTaskId = -1
        }
        Bukkit.broadcastMessage("§7게임 초기화...")
    }

    fun isRunning(): Boolean = phase == GamePhase.RUNNING
    
    fun isWaiting(): Boolean = phase == GamePhase.WAITING
    
    fun isEnded(): Boolean = phase == GamePhase.END

    fun getElapsedSeconds(): Long {
        if (!isRunning()) return 0L
        return (System.currentTimeMillis() - gameStartTime) / 1000
    }

    fun getRemainingSeconds(): Long {
        if (!isRunning()) return 0L
        val remaining = GAME_DURATION_SECONDS - getElapsedSeconds()
        return if (remaining > 0) remaining else 0L
    }

    fun getProgress(): Float {
        if (!isRunning()) return 0f
        return (getElapsedSeconds().toFloat() / GAME_DURATION_SECONDS).coerceIn(0f, 1f)
    }

    fun setTimerTaskId(taskId: Int) {
        timerTaskId = taskId
    }
}
