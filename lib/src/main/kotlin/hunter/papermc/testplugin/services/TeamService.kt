package hunter.papermc.testplugin.services

import hunter.papermc.testplugin.components.TeamType

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

class TeamService(
    private val plugin: JavaPlugin,
    private val scoreboard: Scoreboard
) {
    private val teamMap = mutableMapOf<UUID, TeamType>()
    private val teamMembers = mutableMapOf<TeamType, MutableSet<UUID>>()

    private val file = File(plugin.dataFolder, "team-state.yml")
    private val config = YamlConfiguration()

    init {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }
        if (file.exists()) {
            config.load(file)
            loadTeams()
        }
    }

    fun getTeam(player: Player): TeamType? =
    teamMap[player.uniqueId]

    fun getTeam(uuid: UUID): TeamType? =
        teamMap[uuid]

    fun getTeamMembers(team: TeamType): Set<UUID> =
    teamMembers[team] ?: emptySet()

    fun assign(player: Player, team: TeamType) {
        val uuid = player.uniqueId

        // 기존 팀에서 스코어보드 제거
        teamMap[uuid]?.let { oldTeam ->
            teamMembers[oldTeam]?.remove(uuid)
            scoreboard.getTeam(oldTeam.name)?.removeEntry(player.name)
        }

        // 새 팀 등록
        teamMap[uuid] = team
        teamMembers.getOrPut(team) { mutableSetOf() }.add(uuid)

        // 스코어보드에 팀 설정 적용
        applyTeamToScoreboard(player, team)
    }

    private fun applyTeamToScoreboard(player: Player, team: TeamType) {
        val teamName = team.name
        val scoreboardTeam = scoreboard.getTeam(teamName) ?: scoreboard.registerNewTeam(teamName)
        
        // 팀 색상 설정
        val teamColor = when (team) {
            TeamType.YELLOW -> ChatColor.YELLOW
            TeamType.BLUE -> ChatColor.CYAN
        }
        scoreboardTeam.color = teamColor
        
        // 팀 칭호(prefix) 설정
        val teamDisplayName = when (team) {
            TeamType.YELLOW -> "§e[노랑] "
            TeamType.BLUE -> "§9[하늘] "
        }
        scoreboardTeam.setPrefix(teamDisplayName)
        
        // 플레이어를 스코어보드 팀에 추가
        scoreboardTeam.addEntry(player.name)
        
        // 플레이어의 스코어보드를 메인 스코어보드로 설정
        player.scoreboard = scoreboard
    }

    fun remove(player: Player) {
        val uuid = player.uniqueId
        teamMap[uuid]?.let { team ->
            teamMembers[team]?.remove(uuid)
            scoreboard.getTeam(team.name)?.removeEntry(player.name)
        }
        teamMap.remove(uuid)
    }

    fun applyTeamToPlayer(player: Player) {
        val team = teamMap[player.uniqueId] ?: return
        applyTeamToScoreboard(player, team)
    }

    private fun loadTeams() {
        val section = config.getConfigurationSection("players") ?: return
        for (uuidStr in section.getKeys(false)) {
            val teamName = section.getString(uuidStr) ?: continue
            val team = runCatching {
                TeamType.valueOf(teamName)
            }.getOrNull() ?: continue

            val uuid = UUID.fromString(uuidStr)
            teamMap[uuid] = team
            teamMembers.getOrPut(team) { mutableSetOf() }.add(uuid)
        }
    }

    fun saveTeams() {
        config.set("players", null)
        teamMap.forEach { (uuid, team) ->
            config.set("players.$uuid", team.name)
        }
        config.save(file)
    }

    fun resetAll() {
        teamMap.clear()
    }

    fun listAll(sender: CommandSender) {
        if (teamMap.isEmpty()) {
            sender.sendMessage("§7[TEAM] 팀에 배정된 플레이어가 없습니다.")
            return
        }

        sender.sendMessage("§6[TEAM LIST]")
        teamMap.forEach { (uuid, team) ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
            sender.sendMessage("§f$name → §b${team.name}")
        }
    }
}
