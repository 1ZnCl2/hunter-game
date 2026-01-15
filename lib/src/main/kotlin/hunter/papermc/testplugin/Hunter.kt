package hunter.papermc.testplugin

import hunter.papermc.testplugin.commands.TeamCommand
import hunter.papermc.testplugin.commands.TeamListCommand
import hunter.papermc.testplugin.listeners.HunterCraftListener
import hunter.papermc.testplugin.listeners.HunterUsingListener
import hunter.papermc.testplugin.listeners.PlayerLifecycleListener
import hunter.papermc.testplugin.recipes.HunterItemRecipes
import hunter.papermc.testplugin.services.HunterTrackingService
import hunter.papermc.testplugin.services.TeamService
import hunter.papermc.testplugin.services.SwitchHunterService
import hunter.papermc.testplugin.services.PlayerStateService
import hunter.papermc.testplugin.schedulers.HunterTrackingSchedulers
import hunter.papermc.testplugin.schedulers.GameTimerScheduler
import hunter.papermc.testplugin.usecases.SwitchHunterUsecase
import hunter.papermc.testplugin.usecases.HunterTrackingUsecase
import hunter.papermc.testplugin.usecases.GameControlUsecase
import hunter.papermc.testplugin.usecases.GamePausingUsecase
import hunter.papermc.testplugin.commands.GameManageCommand
import hunter.papermc.testplugin.services.GameStateService

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
    private lateinit var gameTimerScheduler: GameTimerScheduler

    override fun onEnable() {
        logger.info("Hunter Plugin is Activated")
        Bukkit.getPluginManager().registerEvents(this, this)

        // 서비스
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        teamService = TeamService(this, scoreboard)
        val trackingService = HunterTrackingService(teamService)
        val playerStateService = PlayerStateService(this)
        val switchHunterService = SwitchHunterService()
        val gameStateService = GameStateService()

        // 레시피
        HunterItemRecipes.register(this)
        
        // Usecase 생성
        val trackingUsecase = HunterTrackingUsecase(
            trackingService,
            playerStateService,
            teamService
        )
        
        val switchHunterUsecase = SwitchHunterUsecase(
            teamService,
            playerStateService,
            switchHunterService,
            trackingUsecase
        )

        val gameControlUsecase = GameControlUsecase(
            gameStateService
        )

        val gamePausingUsecase = GamePausingUsecase(
            gameStateService
        )

        // 스케줄러
        val trackingSchedulers = HunterTrackingSchedulers(trackingUsecase)
        trackingSchedulers.runTaskTimer(this, 0L, 20L) // 1초 간격

        // 게임 타이머 스케줄러
        gameTimerScheduler = GameTimerScheduler(gameStateService, gameControlUsecase)
        val timerTaskId = gameTimerScheduler.runTaskTimer(this, 0L, 20L).taskId
        gameStateService.setTimerTaskId(timerTaskId)

        // 리스너
        server.pluginManager.registerEvents(
            HunterCraftListener(switchHunterUsecase), this
        )
        server.pluginManager.registerEvents(
            HunterUsingListener(playerStateService, trackingUsecase), this
        )
        server.pluginManager.registerEvents(
            PlayerLifecycleListener(trackingUsecase), this
        )

        // 커맨드
        getCommand("team")?.setExecutor(
            TeamCommand(teamService)
        )
        getCommand("teamlist")?.setExecutor(
            TeamListCommand(teamService)
        )
        getCommand("game").setExecutor(
            GameManageCommand(gameControlUsecase, gamePausingUsecase)
        )
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage(Component.text("Hello, ${event.player.name}!"))
        // 저장된 팀이 있으면 스코어보드에 적용
        teamService.applyTeamToPlayer(event.player)
    }
}