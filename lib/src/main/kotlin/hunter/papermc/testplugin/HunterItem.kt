/*
package hunter.papermc.testplugin

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.command.CommandSender
import org.bukkit.command.Command

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.entity.Player
class HunterItem(private val plugin: Hunter) : Listener {
    private val trackingPlayers = mutableSetOf<Player>()

    init {
        object : BukkitRunnable() {
            override fun run() {
                    trackingPlayers.forEach { player ->
                        val targets = Bukkit.getOnlinePlayers().filter { it != player }
                        if (targets.isNotEmpty()) {
                            val target = targets.random()
                            player.compassTarget = target.location
                            player.sendTitle("§6[나침반]", "§e${target.name}의 위치를 추적합니다.", 10, 40, 10)
                            player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                            player.world.spawnParticle(Particle.TOTEM_OF_UNDYING, player.location, 100)
                        }
                    }
                }
            }.runTaskTimer(plugin, 20L, 1200L)
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val meta = event.recipe.result.itemMeta ?: return
        val hunterPlayer = event.whoClicked as? Player ?: return

        plugin.logger.info("Item is Crafted")

        if (meta.hasCustomModelData() && meta.customModelData == 1001) {
            plugin.logger.info("Hunter is BORN")
            Bukkit.getOnlinePlayers().forEach { p ->
                p.sendTitle(
                    "§7새로운 술래가 탄생했습니다!",
                    "§f${hunterPlayer.name}를 피하세요!",
                    10, 60, 10
                )

                val inv = p.inventory
                for (i in 0 until inv.size) {
                    val item = inv.getItem(i) ?: continue
                    val m = item.itemMeta ?: continue

                    if (m.hasCustomModelData() && m.customModelData == 1001) {
                        inv.clear(i)
                        p.sendMessage("§7[시스템] 다른 플레이어가 술래 아이템을 제작해 당신의 것이 사라졌습니다.")
                    }
                }
            }
        }
    }
    
    @EventHandler
    fun onUse(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val meta = item.itemMeta ?: return

        plugin.logger.info("Chasing is activated")

        if (item.type != Material.RECOVERY_COMPASS) return
        if (!meta.hasCustomModelData() || meta.customModelData != 1001) return

        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        if (!trackingPlayers.contains(player)) {
            plugin.logger.info("Hunter is CHASING")
            trackingPlayers.add(player)
            player.sendMessage("§a이제 당신의 나침반으로 1분마다 상대를 추적할 수 있습니다.")
        } else {
            player.sendMessage("§c이미 추적이 활성화되어 있습니다.")
        }
    }
}

*/