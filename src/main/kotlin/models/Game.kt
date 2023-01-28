package com.jakubmeysner.chessed.models

import com.github.bhlangonijr.chesslib.Board
import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.net.URLEncoder
import java.time.Duration
import java.time.Instant

class Game(
    private val plugin: Chessed,
    val arena: Arena,
    val whitePlayer: Player,
    val blackPlayer: Player,
    val time: Time
) {
    val board = Board()

    val analysisLink: String
        get() = "https://chess.com/analysis?fen=${
            URLEncoder.encode(
                board.fen,
                "UTF-8"
            )
        }"

    val whiteTimeEnd: Instant? = Instant.now().plus(time.startDuration)
    val whiteTimeLeft: Duration? = null
    val blackTimeEnd: Instant? = null
    val blackTimeLeft: Duration? = time.startDuration

    val whiteBossBar = Bukkit
        .createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_10)
        .apply {
            addPlayer(whitePlayer)
            addPlayer(blackPlayer)
        }

    val blackBossBar = Bukkit
        .createBossBar("", BarColor.PURPLE, BarStyle.SEGMENTED_10)
        .apply {
            addPlayer(whitePlayer)
            addPlayer(blackPlayer)
        }

    var drawOfferedByWhite = false
    var drawOfferedByBlack = false

    val timeTask = plugin.runTaskTimer(0, 10) {
        val whiteActualTimeLeft = if (whiteTimeEnd != null) {
            Duration.between(Instant.now(), whiteTimeEnd)
        } else {
            whiteTimeLeft ?: Duration.ZERO
        }

        whiteBossBar.setTitle(
            TextComponent("White Time Left: ").apply {
                color = ChatColor.WHITE
                isBold = true
            }.toLegacyText() +
                TextComponent(
                    "${
                        whiteActualTimeLeft.toMinutesPart().toString()
                            .padStart(2, '0')
                    }:${
                        whiteActualTimeLeft.toSecondsPart().toString()
                            .padStart(2, '0')
                    }"
                ).apply {
                    color =
                        if (whiteTimeEnd != null) ChatColor.YELLOW else ChatColor.GRAY
                }.toLegacyText()
        )

        whiteBossBar.progress =
            whiteActualTimeLeft.seconds.toDouble() / time.startDuration.seconds

        val blackActualTimeLeft = if (blackTimeEnd != null) {
            Duration.between(Instant.now(), blackTimeEnd)
        } else {
            blackTimeLeft ?: Duration.ZERO
        }

        blackBossBar.setTitle(
            TextComponent("Black Time Left: ").apply {
                color = ChatColor.DARK_GRAY
                isBold = true
            }.toLegacyText() +
                TextComponent(
                    "${
                        blackActualTimeLeft.toMinutesPart().toString()
                            .padStart(2, '0')
                    }:${
                        blackActualTimeLeft.toSecondsPart().toString()
                            .padStart(2, '0')
                    }"
                ).apply {
                    color =
                        if (blackTimeEnd != null) ChatColor.YELLOW else ChatColor.GRAY
                }.toLegacyText()
        )

        blackBossBar.progress =
            blackActualTimeLeft.seconds.toDouble() / time.startDuration.seconds

        val loser = if (whiteTimeEnd != null && whiteTimeEnd <= Instant.now()) {
            whitePlayer
        } else if (blackTimeEnd != null && blackTimeEnd <= Instant.now()) {
            blackPlayer
        } else {
            null
        }

        if (loser != null) {
            val winner = if (loser == whitePlayer) blackPlayer else whitePlayer

            winner.sendTitle(
                TextComponent("Victory").apply {
                    color = ChatColor.GREEN
                }.toLegacyText(),
                "On time",
                10, 70, 20
            )

            loser.sendTitle(
                TextComponent("Defeat").apply {
                    color = ChatColor.RED
                }.toLegacyText(),
                "On time",
                10, 70, 20
            )

            winner.spigot().sendMessage(
                TextComponent("You won on time with ${loser.name}.").apply {
                    color = ChatColor.GREEN
                }
            )

            loser.spigot().sendMessage(
                TextComponent("You lost on time with ${winner.name}.").apply {
                    color = ChatColor.RED
                }
            )

            end()
        }
    }

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
        timeTask.cancel()
        whiteBossBar.removeAll()
        blackBossBar.removeAll()

        listOf(whitePlayer, blackPlayer).forEach { player ->
            player.spigot().sendMessage(
                TextComponent("Open analysis on Chess.com.").apply {
                    color = ChatColor.BLUE
                    isUnderlined = true
                    clickEvent = ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "$analysisLink&flip=${player == blackPlayer}"
                    )
                }
            )

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
