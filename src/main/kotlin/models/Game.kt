package com.jakubmeysner.chessed.models

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import java.time.Duration

class Game(
    private val plugin: Chessed,
    val arena: Arena,
    val whitePlayer: Player,
    val blackPlayer: Player,
    val time: Time
) {
    init {
        plugin.games.add(this)

        listOf(whitePlayer, blackPlayer).forEach { player ->
            val white = player == whitePlayer

            player.sendTitle(
                TextComponent(if (white) "White" else "Black").apply {
                    color = if (white) ChatColor.WHITE else ChatColor.BLACK
                    isBold = true
                }.toLegacyText(),
                null,
                10, 70, 20
            )

            player.spigot().sendMessage(
                TextComponent("You'll be playing as "),
                TextComponent(if (white) "White" else "Black").apply {
                    color = if (white) ChatColor.WHITE else ChatColor.BLACK
                    isBold = true
                },
                TextComponent(".")
            )

            player.teleport(
                arena.getBlock(
                    Arena.squareSide * 4 + Arena.squareSide / 2,
                    Arena.squareSide * (if (white) 7 else 0) + Arena.squareSide / 2,
                    10
                ).location.apply {
                    yaw = if (white) arena.location.yaw else arena.location.yaw + 180
                }
            )
        }
    }

    enum class Time(
        val startDuration: Duration,
        val incrementDuration: Duration
    ) {
        RAPID(Duration.ofMinutes(10), Duration.ZERO),
        BLITZ(Duration.ofMinutes(3), Duration.ZERO)
    }
}
