package hunter.papermc.testplugin.commands

import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.services.TeamType
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
            sender.sendMessage("§c사용법: /team <red|blue> <player>")
            return true
        }

        val team = runCatching {
            TeamType.valueOf(args[0].uppercase())
        }.getOrNull() ?: run {
            sender.sendMessage("§c존재하지 않는 팀입니다. (red / blue)")
            return true
        }

        val player = Bukkit.getPlayer(args[1]) ?: run {
            sender.sendMessage("❌ 플레이어를 찾을 수 없습니다.")
            return true
        }

        teamService.assign(player, team)
        teamService.saveTeams()

        sender.sendMessage("§a${player.name}님을 ${team.name} 팀에 배정했습니다.")
        return true
    }
}
