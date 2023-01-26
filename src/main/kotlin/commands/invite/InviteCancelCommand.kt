package com.jakubmeysner.chessed.commands.invite

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class InviteCancelCommand(private val plugin: Chessed) : TabExecutor {
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
                TextComponent("Usage: /invite cancel").apply {
                    color = ChatColor.RED
                }
            )
        } else {
            val invite = plugin.invites[sender]

            if (invite == null) {
                sender.spigot().sendMessage(
                    TextComponent("Couldn't find any active invite that you've sent!").apply {
                        color = ChatColor.RED
                    }
                )
            } else {
                invite.cancel()

                sender.spigot().sendMessage(
                    TextComponent("Canceled the invite that you've sent to ${invite.invitee.name}.").apply {
                        color = ChatColor.GREEN
                    }
                )
            }
        }

        return true
    }

}
