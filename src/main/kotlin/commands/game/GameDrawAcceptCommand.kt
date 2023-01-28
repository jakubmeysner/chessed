package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class GameDrawAcceptCommand(private val plugin: Chessed) : TabExecutor {
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
                TextComponent("Usage: /game draw accept").apply {
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
                (sender == game.whitePlayer && !game.drawOfferedByBlack) ||
                (sender == game.blackPlayer && !game.drawOfferedByWhite)
            ) {
                sender.spigot().sendMessage(
                    TextComponent("You've not been offered a draw!").apply {
                        color = ChatColor.RED
                    }
                )
            } else {
                game.draw("by agreement")
            }
        }


        return true
    }
}
