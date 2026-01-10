package hunter.papermc.testplugin.usecases

import hunter.papermc.testplugin.services.GameStateService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GamePausingUsecase(
    private val gameStateService: GameStateService
) {
    /**
     * 게임을 일시정지합니다.
     * 게임이 실행 중일 때만 일시정지할 수 있습니다.
     */
    fun pauseGame(executor: Player? = null): Boolean {
        if (!gameStateService.isRunning()) {
            executor?.sendMessage(Component.text("§c게임이 실행 중이 아닙니다."))
            return false
        }
        
        gameStateService.pauseGame()
        executor?.sendMessage(Component.text("§a게임이 일시정지되었습니다."))
        return true
    }
    
    /**
     * 게임을 재개합니다.
     * 게임이 일시정지 상태(WAITING)일 때만 재개할 수 있습니다.
     */
    fun resumeGame(executor: Player? = null): Boolean {
        if (!gameStateService.isWaiting()) {
            executor?.sendMessage(Component.text("§c게임이 일시정지 상태가 아닙니다."))
            return false
        }
        
        gameStateService.startGame()
        executor?.sendMessage(Component.text("§a게임이 재개되었습니다."))
        return true
    }
    
    /**
     * 게임 일시정지/재개를 토글합니다.
     */
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

