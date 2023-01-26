package com.jakubmeysner.chessed.commands.invite

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class InviteDeclineCommand(private val plugin: Chessed) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when (args.lastIndex) {
            0 -> plugin.invites.values.filter { it.invitee == sender }
                .map { it.inviter.name }

            else -> emptyList()
        }.filter { it.startsWith(args.last()) }
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
        } else if (args.size != 1) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /invite decline <player>").apply {
                    color = ChatColor.RED
                }
            )
        } else {
            val player = Bukkit.getPlayer(args[0])

            if (player?.isOnline != true) {
                sender.spigot().sendMessage(
                    TextComponent("Couldn't find an online player with given name!").apply {
                        color = ChatColor.RED
                    }
                )

                return true
            }

            val invite = plugin.invites[player]

            if (invite == null) {
                sender.spigot().sendMessage(
                    TextComponent("This player hasn't invited you to a game!").apply {
                        color = ChatColor.RED
                    }
                )

                return true
            }

            invite.decline()
        }

        return true
    }

}
