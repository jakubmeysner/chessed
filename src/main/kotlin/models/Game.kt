package com.jakubmeysner.chessed.models

import com.github.bhlangonijr.chesslib.Board
import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.GameMode
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

    var drawOfferedByWhite = false
    var drawOfferedByBlack = false

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

            player.gameMode = GameMode.ADVENTURE
            player.allowFlight = true
            player.isFlying = true

            player.inventory.clear()
            player.inventory.heldItemSlot = 0

            if (white) {
                player.inventory.setItem(0, moveItem)
            }

            player.inventory.setItem(7, drawItem)
            player.inventory.setItem(8, resignItem)
        }
    }

    fun draw() {
        listOf(whitePlayer, blackPlayer).forEach { player ->
            player.sendTitle(
                TextComponent("Draw").apply {
                    color = ChatColor.GRAY
                }.toLegacyText(),
                "By agreement",
                10, 70, 20
            )

            player.spigot().sendMessage(
                TextComponent("The game has ended in a draw by agreement.").apply {
                    color = ChatColor.GRAY
                }
            )
        }

        end()
    }

    fun resign(white: Boolean) {
        val winner = if (white) blackPlayer else whitePlayer
        val loser = if (white) whitePlayer else blackPlayer

        winner.sendTitle(
            TextComponent("Victory").apply {
                color = ChatColor.GREEN
            }.toLegacyText(),
            "By resignation",
            10, 70, 20
        )

        loser.sendTitle(
            TextComponent("Defeat").apply {
                color = ChatColor.RED
            }.toLegacyText(),
            "By resignation",
            10, 70, 20
        )

        winner.spigot().sendMessage(
            TextComponent("You won by resignation with ${loser.name}.").apply {
                color = ChatColor.GREEN
            }
        )

        loser.spigot().sendMessage(
            TextComponent("You lost by resignation with ${winner.name}.").apply {
                color = ChatColor.RED
            }
        )

        end()
    }

    fun end() {
        listOf(whitePlayer, blackPlayer).forEach { player ->
            player.inventory.clear()
            player.location.world?.spawnLocation?.let { player.teleport(it) }
            player.isFlying = false
            player.allowFlight = false
        }

        plugin.games.remove(this)
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
