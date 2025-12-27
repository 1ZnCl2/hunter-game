package hunter.papermc.testplugin

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

object TeamManage : Listener {
    fun setupTeam() {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard

        val blue = scoreboard.getTeam("Blue") ?: scoreboard.registerNewTeam("Blue")
        blue.prefix = "§b[BLUE] "
        blue.color = ChatColor.AQUA
        blue.setAllowFriendlyFire(false)

        val yellow = scoreboard.getTeam("Yellow") ?: scoreboard.registerNewTeam("Yellow")
        yellow.prefix = "§9[YELLOW] "
        yellow.color = ChatColor.YELLOW
        yellow.setAllowFriendlyFire(false)
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val team = player.scoreboard.getEntryTeam(player.name)
        val color = team?.color?.toString() ?: "§f" // 기본 흰색

        event.format = "$color${player.name}§f: ${event.message}"
    }

    fun assignPlayerToTeam(player: Player, teamName: String) {
        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
        if (team != null) {
            team.addEntry(player.name)
            player.sendMessage("✅ ${team.name} 팀에 배정되었습니다.")
        } else {
            player.sendMessage("❌ 팀 '$teamName'이 존재하지 않습니다.")
        }
    }
}
