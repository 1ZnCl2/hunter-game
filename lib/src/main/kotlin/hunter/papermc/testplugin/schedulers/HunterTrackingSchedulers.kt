package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.services.HunterTrackingService
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

class HunterTrackingSchedulers(
    private val service: HunterTrackingService
) : BukkitRunnable() {
    fun startTracking(hunter: Player) {
    object : BukkitRunnable() {
        override fun run() {
            if (!hunter.isOnline) {
                cancel()
                return
            }

            val target = HunterTrackingService.getNearestEnemy(hunter)

            if (target == null || !target.isOnline) {
                hunter.sendActionBar(
                    Component.text("§7추적 대상이 같은 차원에 없습니다")
                    )
                return
            }

            hunter.compassTarget = target.location
            HunterTrackingService.updateCompassTarget(hunter, target)
                
            hunter.sendActionBar(
                    Component.text("§c추적 중: ${target.name}")
                )
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1초마다 갱신
    }
}
