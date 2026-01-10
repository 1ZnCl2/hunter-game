package hunter.papermc.testplugin.services

import hunter.papermc.testplugin.components.GamePhase
import org.bukkit.Bukkit

class GameStateService {
    var phase: GamePhase = GamePhase.WAITING
        private set

    fun startGame() {
        if (phase != GamePhase.WAITING) return
        phase = GamePhase.RUNNING

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
        Bukkit.broadcastMessage("§7게임 초기화...")
    }

    fun isRunning(): Boolean = phase == GamePhase.RUNNING
    
    fun isWaiting(): Boolean = phase == GamePhase.WAITING
    
    fun isEnded(): Boolean = phase == GamePhase.END
}
