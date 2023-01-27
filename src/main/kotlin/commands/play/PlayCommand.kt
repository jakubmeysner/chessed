package com.jakubmeysner.chessed.commands.play

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Game
import com.jakubmeysner.chessed.models.Invite
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class PlayCommand(private val plugin: Chessed) : TabExecutor {
    private val timeOptions = Game.Time.values().map { it.name.lowercase() }
    private val playAsOptions = listOf("random", "white", "black")

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when (args.lastIndex) {
            0 -> Bukkit.getOnlinePlayers().filter { player ->
                player != sender && plugin.games.none {
                    it.whitePlayer == player || it.blackPlayer == player
                }
            }.map { it.name }

            1 -> timeOptions
            2 -> playAsOptions
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
        } else if (args.size !in 1..3) {
            sender.spigot().sendMessage(
                TextComponent(
                    "Usage: /play <player> [${
                        timeOptions.joinToString("|")
                    }] [${playAsOptions.joinToString("|")}]"
                ).apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.lastIndex >= 1 && args[1] !in timeOptions) {
            sender.spigot().sendMessage(
                TextComponent(
                    "Argument <time> must be ${
                        timeOptions.joinToString(
                            ", "
                        )
                    }!"
                ).apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.lastIndex >= 2 && args[2] !in playAsOptions) {
            sender.spigot().sendMessage(
                TextComponent(
                    "Argument <playAs> must be ${
                        playAsOptions.joinToString(
                            ", "
                        )
                    }!"
                ).apply {
                    color = ChatColor.RED
                }
            )
        } else if (plugin.invites.containsKey(sender)) {
            sender.spigot().sendMessage(
                TextComponent("You've already sent an invite to a game!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (plugin.games.any { it.whitePlayer == sender || it.blackPlayer == sender }) {
            sender.spigot().sendMessage(
                TextComponent("You're already in a game!").apply {
                    color = ChatColor.RED
                }
            )
        } else {
            val player = Bukkit.getPlayer(args[0])

            if (player == sender) {
                sender.spigot().sendMessage(
                    TextComponent("You can't play with yourself!").apply {
                        color = ChatColor.RED
                    }
                )
            } else if (player?.isOnline != true) {
                sender.spigot().sendMessage(
                    TextComponent("Couldn't find an online player with given name!").apply {
                        color = ChatColor.RED
                    }
                )
            } else if (plugin.games.any { it.whitePlayer == sender || it.blackPlayer == sender }) {
                sender.spigot().sendMessage(
                    TextComponent("You can't invite that player as they're already in a game!").apply {
                        color = ChatColor.RED
                    }
                )
            } else {
                Invite(
                    plugin,
                    sender,
                    player,
                    Game.Time.valueOf(args.getOrElse(1) { timeOptions.first() }
                        .uppercase()),
                    args.getOrNull(2).let {
                        when (it) {
                            "white" -> true
                            "black" -> false
                            else -> null
                        }
                    }
                )
            }
        }

        return true
    }
}
