package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class GameHistoryCommand(private val plugin: Chessed) : TabExecutor {
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
                TextComponent("Usage: /game history").apply {
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
            } else {
                sender.openBook(
                    ItemStack(Material.WRITTEN_BOOK).apply {
                        itemMeta = (itemMeta as BookMeta).apply {
                            title = "History"
                            author = "Chess"

                            pages = game.board.backup
                                .asSequence()
                                .map { it.move }
                                .chunked(2)
                                .mapIndexed { index, moves ->
                                    "${index + 1}. ${
                                        moves[0].from.toString().lowercase()
                                    }${
                                        moves[0].to.toString().lowercase()
                                    } ${
                                        moves.getOrNull(
                                            1
                                        )
                                            ?.let {
                                                it.from.toString().lowercase() + it.to.toString().lowercase()
                                            } ?: ""
                                    }".trim()
                                }
                                .chunked(14)
                                .map { it.joinToString("\n") }
                                .ifEmpty { sequenceOf("Empty.") }
                                .toList()
                        }
                    }
                )
            }
        }

        return true
    }
}
