package hunter.papermc.testplugin.recipes

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

object HunterItemRecipes {
    fun register(plugin: JavaPlugin) {
        val item = ItemStack(Material.RECOVERY_COMPASS)
        val meta = item.itemMeta
        meta.setDisplayName("§c술래 아이템")
        meta.lore = listOf("§7술래의 증표. 1분 간격으로 상대 팀의 방향을 보여줍니다.")
        meta.setCustomModelData(1001)
        item.itemMeta = meta

        val key = NamespacedKey(plugin, "hunter_switch_item")
        val recipe = ShapedRecipe(key, item)

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

        plugin.server.addRecipe(recipe)
    }
}
