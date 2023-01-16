package com.jakubmeysner.chessed.commands

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ArenaCommand(private val plugin: Chessed) : TabExecutor {
    private val subcommands = mapOf(
        "add" to ArenaAddCommand(plugin)
    )

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return subcommands.keys.filter { it.startsWith(args.last()) }
        }

        return subcommands[args[0]]
            ?.onTabComplete(sender, command, label, args.copyOfRange(1, args.size))
            ?: listOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return subcommands[args[0]]
            ?.onCommand(sender, command, label, args.copyOfRange(1, args.size))
            ?: let {
                sender.spigot().sendMessage(
                    *ComponentBuilder("Usage: /arena (add) ...").color(ChatColor.RED).create()
                )

                return true
            }
    }
}
