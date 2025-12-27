package hunter.papermc.testplugin.commands

import hunter.papermc.testplugin.services.TeamService
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class TeamListCommand(
    private val teamService: TeamService
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        teamService.listAll(sender)
        return true
    }
}
