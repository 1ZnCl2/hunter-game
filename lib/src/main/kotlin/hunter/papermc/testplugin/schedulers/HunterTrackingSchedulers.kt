package hunter.papermc.testplugin.schedulers

import hunter.papermc.testplugin.usecases.HunterTrackingUsecase
import org.bukkit.scheduler.BukkitRunnable

class HunterTrackingSchedulers(
    private val trackingUsecase: HunterTrackingUsecase
) : BukkitRunnable() {
    override fun run() {
        // 모든 추적 중인 헌터의 추적 상태 업데이트
        trackingUsecase.updateAllTracking()
    }
}
