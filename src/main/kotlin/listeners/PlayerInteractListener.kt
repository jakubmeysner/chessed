package com.jakubmeysner.chessed.listeners

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Game
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractListener(private val plugin: Chessed) : Listener {
    @EventHandler
    @Suppress("unused")
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (
            plugin.games.any {
                it.whitePlayer == event.player || it.blackPlayer == event.player
            }
        ) {
            event.isCancelled = true

            if (
                event.action == Action.RIGHT_CLICK_AIR ||
                event.action == Action.RIGHT_CLICK_BLOCK
            ) {
                when (event.item) {
                    Game.historyItem -> {
                        event.player.performCommand("game history")
                    }

                    Game.drawItem -> {
                        event.player.performCommand("game draw offer")
                    }

                    Game.resignItem -> {
                        event.player.performCommand("game resign")
                    }
                }
            }
        }
    }
}
