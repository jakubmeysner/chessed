package com.jakubmeysner.chessed.listeners

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Game
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractListener(private val plugin: Chessed) : Listener {
    @EventHandler
    @Suppress("unused")
    fun onPlayerInteract(event: PlayerInteractEvent) {
        when (event.item) {
            Game.resignItem -> {
                event.isCancelled = true
                event.player.performCommand("game resign")
            }
        }
    }
}
