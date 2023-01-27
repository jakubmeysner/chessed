package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class GameDrawOfferCommand(private val plugin: Chessed) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return emptyList()
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
        } else if (args.isNotEmpty()) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /game draw offer").apply {
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
                (sender == game.whitePlayer && game.drawOfferedByWhite) ||
                (sender == game.blackPlayer && game.drawOfferedByBlack)
            ) {
                sender.spigot().sendMessage(
                    TextComponent("You've already offered a draw!").apply {
                        color = ChatColor.RED
                    }
                )
            } else if (
                (sender == game.whitePlayer && game.drawOfferedByBlack) ||
                (sender == game.blackPlayer && game.drawOfferedByWhite)
            ) {
                game.draw()
            } else {
                if (sender == game.whitePlayer) {
                    game.drawOfferedByWhite = true
                } else {
                    game.drawOfferedByBlack = true
                }

                val opponent =
                    if (sender == game.whitePlayer) game.blackPlayer
                    else game.whitePlayer

                opponent.spigot().sendMessage(
                    TextComponent("You've been offered a draw.\n").apply {
                        color = ChatColor.AQUA
                    },
                    TextComponent("[ACCEPT]").apply {
                        color = ChatColor.YELLOW
                        isBold = true

                        clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/game draw accept"
                        )
                    },
                    TextComponent(" "),
                    TextComponent("[DECLINE]").apply {
                        color = ChatColor.GRAY
                        isBold = true

                        clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/game draw decline"
                        )
                    }
                )

                sender.spigot().sendMessage(
                    TextComponent("You've offered a draw.").apply {
                        color = ChatColor.GREEN
                    }
                )
            }
        }
        return true
    }
}
