package hunter.papermc.testplugin.command

import hunter.papermc.testplugin.service.TeamService
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class TeamCommand(
    private val teamService: TeamService
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (args.size != 2) {
            sender.sendMessage("사용법: /team <blue|red> <player>")
            return true
        }

        val player = Bukkit.getPlayer(args[1])
            ?: run {
                sender.sendMessage("❌ 플레이어를 찾을 수 없습니다.")
                return true
            }

        teamService.assign(player, args[0])
        return true
    }
}
