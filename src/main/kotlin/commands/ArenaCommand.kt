package com.jakubmeysner.chessed.commands

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ArenaCommand(private val plugin: Chessed) : TabExecutor {
    private val subcommands = mapOf(
        "add" to ArenaAddCommand(plugin),
        "build" to ArenaBuildCommand(plugin),
        "remove" to ArenaRemoveCommand(plugin)
    )

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
            ) ?: listOf()
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
            TextComponent("Usage: /arena (${subcommands.keys.joinToString("|")}) ...").apply {
                color = ChatColor.RED
            }
        )

        return true
    }
}
