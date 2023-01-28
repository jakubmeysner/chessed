package com.jakubmeysner.chessed.commands.game

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class GameMoveCommand(private val plugin: Chessed) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        val game = plugin.games.find {
            it.whitePlayer == sender || it.blackPlayer == sender
        } ?: return emptyList()

        if (game.board.sideToMove == Side.WHITE && sender != game.whitePlayer) {
            return emptyList()
        }

        val legalMoves = game.board.legalMoves()

        return when (args.lastIndex) {
            0 -> legalMoves.map { it.from.name.lowercase() }

            1 -> legalMoves.filter {
                it.from.name.lowercase() == args[0].lowercase()
            }.map { it.to.name.lowercase() }

            2 -> legalMoves.filter {
                it.from.name.lowercase() == args[0].lowercase() &&
                    it.to.name.lowercase() == args[1].lowercase()
            }.map { it.promotion.name.lowercase() }

            else -> emptyList()
        }.filter { it.lowercase().startsWith(args.last().lowercase()) }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.spigot().sendMessage(
                TextComponent("This command may only be used by players!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.size !in 2..3) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /game move <from> <to> [<promoteTo>]").apply {
                    color = ChatColor.RED
                }
            )
        } else {
            val game = plugin.games.find {
                it.whitePlayer == sender || it.blackPlayer == sender
            }

            if (game == null) {
                sender.spigot().sendMessage(
                    TextComponent("You're not in a game!").apply {
                        color = ChatColor.RED
                    }
                )
            } else if (
                game.board.sideToMove == Side.WHITE &&
                sender != game.whitePlayer
            ) {
                sender.spigot().sendMessage(
                    TextComponent("It's not your turn!").apply {
                        color = ChatColor.RED
                    }
                )
            } else {
                val move = game.board.legalMoves().find {
                    it.from.name.lowercase() == args[0].lowercase() &&
                        it.to.name.lowercase() == args[1].lowercase() &&
                        if (args.lastIndex == 2) {
                            it.promotion.name.lowercase() == args[2].lowercase()
                        } else {
                            it.promotion.pieceType == PieceType.QUEEN ||
                                it.promotion == Piece.NONE
                        }
                }

                if (move == null) {
                    sender.spigot().sendMessage(
                        TextComponent("This is not a legal move!").apply {
                            color = ChatColor.RED
                        }
                    )
                } else {
                    game.move(move)
                }
            }
        }

        return true
    }
}
