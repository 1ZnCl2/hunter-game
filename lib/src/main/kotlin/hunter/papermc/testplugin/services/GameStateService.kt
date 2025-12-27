package hunter.papermc.testplugin.services

import org.bukkit.Bukkit
enum class GamePhase {
    WAITING,
    RUNNING,
    END
}
// 나중에 빼도 될 듯?

class GameStateService {
    var phase: GamePhase = GamePhase.WAITING
        private set

    fun startGame() {
        if (phase != GamePhase.WAITING) return
        phase = GamePhase.RUNNING

        Bukkit.broadcastMessage("§a[GAME] 게임이 시작되었습니다!")
    }

    fun endGame() {
        if (phase != GamePhase.RUNNING) return
        phase = GamePhase.END

        Bukkit.broadcastMessage("§c[GAME] 게임이 종료되었습니다.")
    }

    fun resetGame() {
        phase = GamePhase.WAITING
        Bukkit.broadcastMessage("§7[GAME] 게임이 초기화되었습니다.")
    }

    fun isRunning(): Boolean = phase == GamePhase.RUNNING
}