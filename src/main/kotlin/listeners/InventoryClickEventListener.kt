package com.jakubmeysner.chessed.listeners

import com.jakubmeysner.chessed.Chessed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickEventListener(private val plugin: Chessed) : Listener {
    @EventHandler
    @Suppress("unused")
    fun onInventoryClick(event: InventoryClickEvent) {
        if (
            plugin.games.any {
                it.whitePlayer == event.whoClicked ||
                    it.blackPlayer == event.whoClicked
            }
        ) {
            event.isCancelled = true
        }
    }
}
