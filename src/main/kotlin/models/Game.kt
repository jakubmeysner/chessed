package com.jakubmeysner.chessed.models

import com.github.bhlangonijr.chesslib.Board
import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration

class Game(
    private val plugin: Chessed,
    val arena: Arena,
    val whitePlayer: Player,
    val blackPlayer: Player,
    val time: Time
) {
    val board = Board()

    init {
        plugin.games.add(this)

        listOf(whitePlayer, blackPlayer).forEach { player ->
            val white = player == whitePlayer

            player.sendTitle(
                TextComponent(if (white) "White" else "Black").apply {
                    color = if (white) ChatColor.WHITE else ChatColor.DARK_GRAY
                }.toLegacyText(),
                "Playing as...",
                10, 70, 20
            )

            player.spigot().sendMessage(
                TextComponent("You'll be playing as "),
                TextComponent(if (white) "White" else "Black").apply {
                    color = if (white) ChatColor.WHITE else ChatColor.DARK_GRAY
                    isBold = true
                },
                TextComponent(".")
            )

            player.teleport(
                arena.getBlock(
                    Arena.squareSide * 4 + Arena.squareSide / 2,
                    Arena.squareSide * (if (white) 0 else 7) + Arena.squareSide / 2,
                    10
                ).location.apply {
                    yaw =
                        if (white) arena.location.yaw
                        else arena.location.yaw + 180
                }
            )

            player.allowFlight = true
            player.isFlying = true

            player.inventory.clear()

            if (white) {
                player.inventory.setItem(0, moveItem)
            }

            player.inventory.setItem(7, drawItem)
            player.inventory.setItem(8, resignItem)
        }
    }

    companion object {
        val moveItem = ItemStack(Material.REDSTONE_TORCH).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(
                    TextComponent("Move").apply {
                        color = ChatColor.AQUA
                    }.toLegacyText()
                )
            }
        }

        val drawItem = ItemStack(Material.GRAY_BANNER).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(
                    TextComponent("Draw").apply {
                        color = ChatColor.GRAY
                    }.toLegacyText()
                )
            }
        }

        val resignItem = ItemStack(Material.RED_BANNER).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(
                    TextComponent("Resign").apply {
                        color = ChatColor.RED
                    }.toLegacyText()
                )
            }
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
