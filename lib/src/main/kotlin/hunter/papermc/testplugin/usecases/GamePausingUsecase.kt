package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.services.GameStateService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GamePausingUsecase(
    private val gameStateService: GameStateService
) {
    fun pauseGame(executor: Player? = null): Boolean {
        if (!gameStateService.isRunning()) {
            executor?.sendMessage(Component.text("§c게임이 실행 중이 아닙니다."))
            return false
        }
        
        gameStateService.pauseGame()
        executor?.sendMessage(Component.text("§a게임이 일시정지되었습니다."))
        return true
    }
    
    fun resumeGame(executor: Player? = null): Boolean {
        if (!gameStateService.isWaiting()) {
            executor?.sendMessage(Component.text("§c게임이 일시정지 상태가 아닙니다."))
            return false
        }
        
        gameStateService.startGame()
        executor?.sendMessage(Component.text("§a게임이 재개되었습니다."))
        return true
    }
    
    fun togglePause(executor: Player? = null): Boolean {
        return if (gameStateService.isRunning()) {
            pauseGame(executor)
        } else if (gameStateService.isWaiting()) {
            resumeGame(executor)
        } else {
            executor?.sendMessage(Component.text("§c게임이 종료된 상태입니다. 게임을 초기화하세요."))
            false
        }
    }
}

