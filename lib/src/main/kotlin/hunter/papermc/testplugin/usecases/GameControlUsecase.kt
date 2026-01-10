package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.services.GameStateService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GameControlUsecase(
    private val gameStateService: GameStateService
) {
    /**
     * 게임을 시작합니다.
     * 게임이 WAITING 상태일 때만 시작할 수 있습니다.
     */
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
    
    /**
     * 게임을 종료합니다.
     * 게임이 RUNNING 상태일 때만 종료할 수 있습니다.
     */
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
    
    /**
     * 게임을 초기화합니다.
     * 어떤 상태에서든 초기화할 수 있습니다.
     */
    fun resetGame(executor: Player? = null): Boolean {
        gameStateService.resetGame()
        executor?.sendMessage(Component.text("§7게임이 초기화되었습니다."))
        return true
    }
    
    /**
     * 현재 게임 상태를 확인합니다.
     */
    fun getGameStatus(): String {
        return when {
            gameStateService.isWaiting() -> "§e대기 중"
            gameStateService.isRunning() -> "§a실행 중"
            gameStateService.isEnded() -> "§c종료됨"
            else -> "§7알 수 없음"
        }
    }
    
    /**
     * 현재 게임 상태 정보를 플레이어에게 전달합니다.
     */
    fun sendStatus(executor: Player) {
        executor.sendMessage(Component.text("§7현재 게임 상태: ${getGameStatus()}"))
    }
}

