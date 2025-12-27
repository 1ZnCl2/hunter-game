package hunter.papermc.testplugin.services

import org.bukkit.entity.Player
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

enum class TeamType {
    YELLOW,
    BLUE
}

class TeamService(
    private val plugin: JavaPlugin
) {
    private val teamMap = mutableMapOf<UUID, TeamType>()

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

    fun assign(player: Player, team: TeamType) {
        teamMap[player.uniqueId] = team
    }

    fun remove(player: Player) {
        teamMap.remove(player.uniqueId)
    }

    private fun loadTeams() {
        val section = config.getConfigurationSection("players") ?: return
        for (uuidStr in section.getKeys(false)) {
            val teamName = section.getString(uuidStr) ?: continue
            val team = runCatching {
                TeamType.valueOf(teamName)
            }.getOrNull() ?: continue

            teamMap[UUID.fromString(uuidStr)] = team
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
