package com.jakubmeysner.chessed.commands

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

abstract class ParentCommand(
    val name: String,
    private val subcommands: Map<String, TabExecutor>
) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return if (args.lastIndex == 0) {
            subcommands.keys.filter { it.startsWith(args.last()) }
        } else {
            subcommands[args[0]]?.onTabComplete(
                sender, command, label, args.sliceArray(1..args.lastIndex)
            ) ?: emptyList()
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        subcommands[args.getOrNull(0)]?.let {
            return it.onCommand(
                sender,
                command,
                label,
                args.sliceArray(1..args.lastIndex)
            )
        }

        sender.spigot().sendMessage(
            TextComponent(
                "Usage: /$name (${
                    subcommands.keys.joinToString("|")
                }) ..."
            ).apply {
                color = ChatColor.RED
            }
        )

        return true
    }
}
