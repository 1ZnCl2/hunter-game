package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.services.GameScoreService
import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.components.TeamType
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class ScoreboardDisplayScheduler(
    private val gameScoreService: GameScoreService,
    private val teamService: TeamService
) : BukkitRunnable() {

    private val scoreboard = Bukkit.getScoreboardManager().newScoreboard()
    private val objective = scoreboard.registerNewObjective(
        "hunter_scores",
        "dummy",
        "§6§l점 수 판 "
    )
    
    init {
        objective.displaySlot = org.bukkit.scoreboard.DisplaySlot.SIDEBAR
    }

    override fun run() {
        updateScoreboard()
    }

    private fun updateScoreboard() {
        val scores = gameScoreService.getAllScores()
        
        objective.scoreList.forEach { score ->
            scoreboard.resetScores(score)
        }

        val sortedScores = scores.entries.sortedByDescending { it.value }
        var index = sortedScores.size

        for ((team, score) in sortedScores) {
            val teamDisplayName = when (team) {
                TeamType.YELLOW -> "§e[노랑팀]"
                TeamType.BLUE -> "§b[하늘팀]"
            }
            
            objective.getScore("$teamDisplayName §f$score").score = index
            index--
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.scoreboard != scoreboard) {
                player.scoreboard = scoreboard
            }
        }
    }

    fun addPlayerToScoreboard(player: org.bukkit.entity.Player) {
        player.scoreboard = scoreboard
    }

    fun removePlayerFromScoreboard(player: org.bukkit.entity.Player) {
        if (player.scoreboard == scoreboard) {
            player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        }
    }
}
