package hunter.papermc.testplugin.commands

import hunter.papermc.testplugin.usecases.GameControlUsecase
import hunter.papermc.testplugin.usecases.GamePausingUsecase
import hunter.papermc.testplugin.services.GameStateService
import hunter.papermc.testplugin.schedulers.GameTimerScheduler
import hunter.papermc.testplugin.components.GamePhase

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class GameManageCommand(
    private val controlUsecase: GameControlUsecase,
    private val pausingUsecase: GamePausingUsecase,
    private val gameStateService: GameStateService,
    private val gameTimerScheduler: GameTimerScheduler,
    private val plugin: JavaPlugin
) : CommandExecutor{
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
        ): Boolean{
            if (args.isEmpty()) {
                sender.sendMessage("§c사용법: /game <start|pause|resume|end|reset|status>")
                return true
            }

            val phase = args[0].lowercase()
            val player = sender as? Player ?: run {
                sender.sendMessage("❌ 이 명령어는 플레이어만 사용할 수 있습니다.")
                return true
            }            

            when (phase) {
                "start" -> {
                    controlUsecase.startGame(player)
                    // 게임 시작 시 타이머 스케줄러 실행
                    if (gameStateService.isRunning()) {
                        val timerTask = gameTimerScheduler.runTaskTimer(plugin, 0L, 20L)
                        gameStateService.setTimerTask(timerTask)
                    }
                }
                "end"   -> controlUsecase.endGame(player)
                "reset" -> controlUsecase.resetGame(player)
    
                "pause" -> pausingUsecase.pauseGame(player)
                "resume" -> pausingUsecase.resumeGame(player)
                "toggle" -> pausingUsecase.togglePause(player)
    
                "status" -> {
                    if (player != null) {
                        controlUsecase.sendStatus(player)
                    } else {
                        sender.sendMessage(controlUsecase.getGameStatus())
                    }
                }
    
                else -> {
                    sender.sendMessage("§c알 수 없는 서브 명령입니다. (start|pause|resume|end|reset|status)")
                    return true
                }
            }

            return true
    }
}