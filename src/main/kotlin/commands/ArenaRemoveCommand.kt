package com.jakubmeysner.chessed.commands

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ArenaRemoveCommand(private val plugin: Chessed) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when (args.lastIndex) {
            0 -> plugin.arenas.keys.toList()
            else -> listOf()
        }.filter { it.startsWith(args.last()) }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.size != 1) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /arena remove <arena>").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args[0] !in plugin.arenas.keys) {
            sender.spigot().sendMessage(
                TextComponent("Argument <arena> must be an existing arena name!").apply {
                    color = ChatColor.RED
                }
            )
        } else {
            plugin.arenas.remove(args[0])

            sender.spigot().sendMessage(
                TextComponent("Removed arena ${args[0]}.").apply {
                    color = ChatColor.GREEN
                }
            )
        }

        return true
    }
}
