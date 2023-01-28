package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Game
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class GameMenuCommand(private val plugin: Chessed) : TabExecutor {
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
                TextComponent("Usage: /game menu").apply {
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
                val inventory = Bukkit.createInventory(null, 54, "Board")

                for (i in 1..(54 + 27)) {
                    if (i % 9 == 0 || i > 72) {
                        if (i > 54) {
                            sender.inventory.setItem(
                                i - 54 + 9 - 1,
                                Game.emptySpaceItem
                            )
                        } else {
                            inventory.setItem(i - 1, Game.emptySpaceItem)
                        }
                    }
                }

                if (sender == game.whitePlayer) {
                    game.whiteMenuInventory = inventory
                } else {
                    game.blackMenuInventory = inventory
                }

                sender.openInventory(inventory)
            }
        }

        return true
    }
}
