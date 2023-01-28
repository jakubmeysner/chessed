package com.jakubmeysner.chessed.models

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
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

    var whiteTimeEnd: Instant? = Instant.now().plus(time.startDuration)
    var whiteTimeLeft: Duration? = null
    var blackTimeEnd: Instant? = null
    var blackTimeLeft: Duration? = time.startDuration

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
        val loser =
            if (whiteTimeEnd?.isBefore(Instant.now()) == true) {
                whitePlayer
            } else if (blackTimeEnd?.isBefore(Instant.now()) == true) {
                blackPlayer
            } else {
                null
            }

        if (loser != null) {
            win(loser != whitePlayer, "on time")
        } else {
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
                    10,
                    Arena.squareSide * (if (white) 0 else 7) + Arena.squareSide / 2
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

    fun move(move: Move) {
        board.doMove(move)

        if (board.isMated) {
            win(board.sideToMove == Side.BLACK, "by checkmate")
        } else if (board.isDraw) {
            draw(
                if (board.isStaleMate) {
                    "by stalemate"
                } else if (board.isRepetition) {
                    "by repetition"
                } else if (board.isInsufficientMaterial) {
                    "by insufficient material"
                } else {
                    "by fifty-move rule"
                }
            )
        } else {
            listOf(whitePlayer, blackPlayer).forEach {
                it.playNote(
                    it.location,
                    if (board.isKingAttacked) Instrument.BANJO
                    else Instrument.BASS_DRUM,
                    Note.flat(0, Note.Tone.A)
                )
            }

            if (board.sideToMove == Side.WHITE) {
                whiteTimeEnd = Instant.now().plus(whiteTimeLeft)
                whiteTimeLeft = null
                blackTimeLeft = Duration.between(Instant.now(), blackTimeEnd)
                blackTimeEnd = null

                whitePlayer.inventory.setItem(0, moveItem)
                blackPlayer.inventory.setItem(0, null)
            } else {
                blackTimeEnd = Instant.now().plus(blackTimeLeft)
                blackTimeLeft = null
                whiteTimeLeft = Duration.between(Instant.now(), whiteTimeEnd)
                whiteTimeEnd = null

                whitePlayer.inventory.setItem(0, null)
                blackPlayer.inventory.setItem(0, moveItem)
            }
        }
    }

    fun win(white: Boolean, reason: String) {
        val winner = if (white) whitePlayer else blackPlayer
        val loser = if (white) blackPlayer else whitePlayer

        winner.sendTitle(
            TextComponent("Victory").apply {
                color = ChatColor.GREEN
            }.toLegacyText(),
            reason.replaceFirstChar { it.uppercase() },
            10, 70, 20
        )

        loser.sendTitle(
            TextComponent("Defeat").apply {
                color = ChatColor.RED
            }.toLegacyText(),
            reason.replaceFirstChar { it.uppercase() },
            10, 70, 20
        )

        winner.spigot().sendMessage(
            TextComponent("You won $reason with ${loser.name}.").apply {
                color = ChatColor.GREEN
            }
        )

        loser.spigot().sendMessage(
            TextComponent("You lost $reason with ${winner.name}.").apply {
                color = ChatColor.RED
            }
        )

        end()

        winner.playNote(
            winner.location,
            Instrument.BELL,
            Note.natural(1, Note.Tone.A)
        )

        loser.playNote(
            loser.location,
            Instrument.GUITAR,
            Note.natural(1, Note.Tone.A)
        )
    }

    fun draw(reason: String) {
        listOf(whitePlayer, blackPlayer).forEach { player ->
            player.sendTitle(
                TextComponent("Draw").apply {
                    color = ChatColor.GRAY
                }.toLegacyText(),
                reason.replaceFirstChar { it.uppercase() },
                10, 70, 20
            )

            player.spigot().sendMessage(
                TextComponent("The game has ended in a draw $reason.").apply {
                    color = ChatColor.GRAY
                }
            )
        }

        end()

        listOf(whitePlayer, blackPlayer).forEach { player ->
            player.playNote(
                player.location,
                Instrument.SNARE_DRUM,
                Note.natural(1, Note.Tone.A)
            )
        }
    }

    fun resign(white: Boolean) {
        win(!white, "by resignation")
    }

    fun end() {
        timeTask.cancel()
        whiteBossBar.removeAll()
        blackBossBar.removeAll()

        listOf(whitePlayer, blackPlayer).forEach { player ->
            player.spigot().sendMessage(
                TextComponent("[Open analysis]").apply {
                    color = ChatColor.BLUE
                    isBold = true

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
