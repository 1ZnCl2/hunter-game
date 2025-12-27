package hunter.papermc.testplugin.services

import org.bukkit.entity.Player
import java.util.UUID
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

enum class PlayerState {
    NORMAL,
    HUNTER,
    PREY,
    DEAD
}

class PlayerStateService(
    private val plugin: JavaPlugin
) {
    private val stateMap = mutableMapOf<UUID, PlayerState>()
    private val file = File(plugin.dataFolder, "player-state.yml")
    private val config = YamlConfiguration()

    init {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }
        if (file.exists()) {
            config.load(file)
            loadStates()
        }
    }

    fun getState(player: Player): PlayerState =
        stateMap[player.uniqueId] ?: PlayerState.NORMAL

    fun setState(player: Player, state: PlayerState) {
        stateMap[player.uniqueId] = state
    }

    private fun loadStates() {
        val section = config.getConfigurationSection("players") ?: return
        for (uuidStr in section.getKeys(false)) {
            val stateName = section.getString(uuidStr) ?: continue
            val state = runCatching {
                PlayerState.valueOf(stateName)
            }.getOrNull() ?: continue

            stateMap[UUID.fromString(uuidStr)] = state
        }
    }

    fun saveStates() {
        config.set("players", null)
        stateMap.forEach { (uuid, state) ->
            config.set("players.$uuid", state.name)
        }
        config.save(file)
    }
}
