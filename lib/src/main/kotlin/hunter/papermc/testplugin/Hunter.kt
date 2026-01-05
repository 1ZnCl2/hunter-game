package hunter.papermc.testplugin
import hunter.papermc.testplugin.commands.TeamCommand
import hunter.papermc.testplugin.commands.TeamListCommand
import hunter.papermc.testplugin.listeners.HunterCraftListener
import hunter.papermc.testplugin.listeners.HunterUsingListener
import hunter.papermc.testplugin.recipes.HunterItemRecipes
import hunter.papermc.testplugin.services.HunterTrackingService
import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.services.SwitchHunterService
import hunter.papermc.testplugin.services.PlayerStateService
import hunter.papermc.testplugin.schedulers.HunterTrackingSchedulers
import hunter.papermc.testplugin.usecases.SwitchHunterUseCase

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Team
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe

class Hunter : JavaPlugin(), Listener {

    private lateinit var teamService: TeamService

    override fun onEnable() {
        logger.info("Hunter Plugin is Activated")
        Bukkit.getPluginManager().registerEvents(this, this)

        // 서비스
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        teamService = TeamService(this, scoreboard)
        val trackingService = HunterTrackingService(teamService)
        val playerStateService = PlayerStateService(this)
        val switchHunterService = SwitchHunterService()

        // 레시피
        HunterItemRecipes.register(this)
        
        // usecase (이건 한국어로 뭐라고 해야 할까...?)
            val switchHunterUseCase = SwitchHunterUseCase(
            teamService,
            playerStateService,
            switchHunterService
        )

        // 스케줄러
        val trackingSchedulers = HunterTrackingSchedulers(trackingService)
        // trackingSchedulers.runTaskTimer(this, 0L, 1200L) // 1분 간격

        // 리스너
        server.pluginManager.registerEvents(
            HunterCraftListener(switchHunterUseCase), this
        )
        server.pluginManager.registerEvents(
            HunterUsingListener(playerStateService, trackingSchedulers), this
        )

        // 커맨드
        getCommand("team")?.setExecutor(
            TeamCommand(teamService)
        )
        getCommand("teamlist")?.setExecutor(
            TeamListCommand(teamService)
        )
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage(Component.text("Hello, ${event.player.name}!"))
        // 저장된 팀이 있으면 스코어보드에 적용
        teamService.applyTeamToPlayer(event.player)
    }

    /* private fun registerHunterRecipe() {
        // 결과물
        val hunterItem = ItemStack(Material.RECOVERY_COMPASS)
        val meta = hunterItem.itemMeta
        meta.setDisplayName("§c술래 아이템")
        meta.lore = listOf("§7술래의 증표. 1분 간격으로 상대 팀의 방향을 보여줍니다.")
        hunterItem.itemMeta = meta

        // 네임스페이스 키
        val key = NamespacedKey(this, "hunter_switch_item")

        // 조합 패턴
        val recipe = ShapedRecipe(key, hunterItem)
        recipe.shape(
            " D ",
            "GSQ",
            " I "
        )
        recipe.setIngredient('Q', Material.QUARTZ_BLOCK)
        recipe.setIngredient('D', Material.DIAMOND_BLOCK)
        recipe.setIngredient('I', Material.IRON_BLOCK)
        recipe.setIngredient('G', Material.GOLD_BLOCK)
        recipe.setIngredient('S', Material.SPIDER_EYE)

        // 서버에 등록
        server.addRecipe(recipe)
    } */
    
/* override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (command.name.equals("team", ignoreCase = true)) {
            if (args.size != 2) {
                sender.sendMessage("사용법: /team <blue|red> <player>")
                return true
            }

            val teamName = args[0].replaceFirstChar { it.uppercaseChar() }
            val player = Bukkit.getPlayer(args[1])
            if (player == null) {
                sender.sendMessage("❌ 플레이어를 찾을 수 없습니다.")
                return true
            }

            TeamManage.assignPlayerToTeam(player, teamName)
            return true
        }

        if (command.name.equals("teamlist", ignoreCase = true)) {
            for (online in Bukkit.getOnlinePlayers()) {
                val team = online.scoreboard.getEntryTeam(online.name)
                val teamName = team?.name ?: "없음"
                sender.sendMessage("${online.name} -> $teamName")
            }
            return true
        }

        return false
    } */
    
}
