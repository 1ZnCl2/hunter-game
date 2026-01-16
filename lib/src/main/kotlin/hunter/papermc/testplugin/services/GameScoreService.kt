package hunter.papermc.testplugin.services

import hunter.papermc.testplugin.components.TeamType
import org.bukkit.Bukkit

class GameScoreService {
    private val teamScores = mutableMapOf<TeamType, Int>()

    init {
        resetScores()
    }

    fun addScore(team: TeamType, points: Int = 1) {
        teamScores[team] = (teamScores[team] ?: 0) + points
    }

    fun getScore(team: TeamType): Int = teamScores[team] ?: 0

    fun getAllScores(): Map<TeamType, Int> = teamScores.toMap()

    fun resetScores() {
        teamScores.clear()
        TeamType.values().forEach { team ->
            teamScores[team] = 0
        }
    }

    fun getWinningTeam(): TeamType? {
        return teamScores.maxByOrNull { it.value }?.key
    }
}
