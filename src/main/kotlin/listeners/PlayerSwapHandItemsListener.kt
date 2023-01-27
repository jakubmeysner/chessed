package com.jakubmeysner.chessed.listeners

import com.jakubmeysner.chessed.Chessed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class PlayerSwapHandItemsListener(private val plugin: Chessed) : Listener {
    @EventHandler
    @Suppress("unused")
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        if (
            plugin.games.any {
                it.whitePlayer == event.player || it.blackPlayer == event.player
            }
        ) {
            event.isCancelled = true
        }
    }
}
