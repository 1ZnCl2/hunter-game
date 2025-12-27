package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.services.HunterTrackingService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class HunterTrackingSchedulers(
    private val service: HunterTrackingService
) : BukkitRunnable() {
    override fun run() {
        service.getTrackingPlayers().forEach { hunter ->
            val target = service.getNearestEnemy(hunter) ?: return@forEach

            if (!target.isOnline) {
                hunter.sendActionBar(
                    Component.text("§7추적할 대상이 없습니다!")
                )
                return@forEach
            }

            service.updateCompassTarget(hunter, target)
            hunter.sendActionBar(
                Component.text("§c추적 중: ${target.name}")
            )
        }
    }
}
