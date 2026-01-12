package hunter.papermc.testplugin.commands

import hunter.papermc.testplugin.usecases.GameControlUsecase
import hunter.papermc.testplugin.usecases.GamePausingUsecase
import hunter.papermc.testplugin.components.GamePhase

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class GameManageCommand(private val controlUsecase: GameControlUsecase,
    private val pausingUsecase: GamePausingUsecase,
) : CommandExecutor{
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
        ): Boolean{
            if (args.size != 2) {
            sender.sendMessage("§c사용법: /phase <wait|start|end>")
            return true
        }

            val phase = runCatching {
            TeamType.valueOf(args[0].uppercase())
        }.getOrNull() ?: run {
            sender.sendMessage("§c상태가 잘못 입력되었습니다.")
            return true
        }

        if (phase == GamePhase.WAITING) {
            controlUsecase.
        }

    }
}