package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.services.GameStateService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GameControlUsecase(
    private val gameStateService: GameStateService
) {
    fun startGame(executor: Player? = null): Boolean {
        if (!gameStateService.isWaiting()) {
            val status = getGameStatus()
            executor?.sendMessage(Component.text("§c게임을 시작할 수 없습니다. 현재 상태: $status"))
            return false
        }
        
        gameStateService.startGame()
        executor?.sendMessage(Component.text("§a게임이 시작되었습니다!"))
        return true
    }
    
    fun endGame(executor: Player? = null): Boolean {
        if (!gameStateService.isRunning()) {
            val status = getGameStatus()
            executor?.sendMessage(Component.text("§c게임을 종료할 수 없습니다. 현재 상태: $status"))
            return false
        }
        
        gameStateService.endGame()
        executor?.sendMessage(Component.text("§c게임이 종료되었습니다."))
        return true
    }
    
    fun resetGame(executor: Player? = null): Boolean {
        gameStateService.resetGame()
        executor?.sendMessage(Component.text("§7게임이 초기화되었습니다."))
        return true
    }
    
    fun getGameStatus(): String {
        return when {
            gameStateService.isWaiting() -> "§e대기 중"
            gameStateService.isRunning() -> "§a실행 중"
            gameStateService.isEnded() -> "§c종료됨"
            else -> "§7알 수 없음"
        }
    }
    
    fun sendStatus(executor: Player) {
        executor.sendMessage(Component.text("§7현재 게임 상태: ${getGameStatus()}"))
    }
}

