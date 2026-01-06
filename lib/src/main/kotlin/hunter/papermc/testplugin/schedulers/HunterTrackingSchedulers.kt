package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.usecases.HunterTrackingUsecase
import org.bukkit.scheduler.BukkitRunnable

class HunterTrackingSchedulers(
    private val trackingUsecase: HunterTrackingUsecase
) : BukkitRunnable() {
    override fun run() {
        trackingUsecase.updateAllTracking()
    }
}
