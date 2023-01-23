package com.jakubmeysner.chessed.commands

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Arena
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Entity

class ArenaAddCommand(private val plugin: Chessed) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when (args.lastIndex) {
            1 -> Bukkit.getWorlds().map { it.name }

            in 2..4 -> if (sender is Entity) {
                return listOf(sender.location.let {
                    when (args.lastIndex) {
                        2 -> it.blockX
                        3 -> it.blockY
                        4 -> it.blockZ
                        else -> throw IllegalStateException()
                    }
                }.toString())
            } else {
                listOf()
            }

            5 -> listOf("90", "0", "-90", "-180")
            else -> listOf()
        }.filter { it.startsWith(args.last()) }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Entity && args.size !in 1..6) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /arena add <arena> [<worldName>] [<x>] [<y>] [<z>] [<yaw>]").apply {
                    color = ChatColor.RED
                }
            )
        } else if (sender !is Entity && args.size != 6) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /arena add <arena> <worldName> <x> <y> <z> <yaw>").apply {
                    color = ChatColor.RED
                }
            )
        } else if (!args[0].matches(Regex("""^\w{3,16}$"""))) {
            sender.spigot().sendMessage(
                TextComponent("Argument <arena> must contain 3-16 alphanumeric characters and underscores!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.size >= 3 && args[2].toIntOrNull() == null) {
            sender.spigot().sendMessage(
                TextComponent("Argument <x> must be an integer!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.size >= 4 && args[3].toIntOrNull() == null) {
            sender.spigot().sendMessage(
                TextComponent("Argument <y> must be an integer!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.size >= 5 && args[4].toIntOrNull() == null) {
            sender.spigot().sendMessage(
                TextComponent("Argument <z> must be an integer!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (
            args.size >= 6 && args[5] !in listOf("90", "0", "-90", "-180")
        ) {
            sender.spigot().sendMessage(
                TextComponent("Argument <yaw> must be 90, 0, -90 or -180!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args[0] in plugin.arenas.keys) {
            sender.spigot().sendMessage(
                TextComponent("Argument <arena> must be a unique arena name!").apply {
                    color = ChatColor.RED
                }
            )
        } else if (args.size >= 2 && Bukkit.getWorld(args[1]) == null) {
            sender.spigot().sendMessage(
                TextComponent("Argument <worldName> must be a world name!").apply {
                    color = ChatColor.RED
                }
            )
        } else {
            val name = args[0]

            val world = args.getOrNull(1)?.let { Bukkit.getWorld(it) }
                ?: (sender as Entity).location.world

            val x =
                args.getOrNull(2)?.toInt() ?: (sender as Entity).location.blockX

            val y =
                args.getOrNull(3)?.toInt() ?: (sender as Entity).location.blockY

            val z =
                args.getOrNull(4)?.toInt() ?: (sender as Entity).location.blockZ

            val yaw = args.getOrNull(5)?.toInt()
                ?: when ((sender as Entity).location.yaw) {
                    in 45.0..<135.0 -> 90
                    in -45.0..<45.0 -> 0
                    in -135.0..<-45.0 -> -90
                    else -> -180
                }

            val location = Location(
                world,
                x.toDouble(),
                y.toDouble(),
                z.toDouble(),
                yaw.toFloat(),
                0F
            )

            val arena = Arena(name, location)
            plugin.arenas[arena.name] = arena

            sender.spigot().sendMessage(
                TextComponent("Created new arena ${arena.name}.").apply {
                    color = ChatColor.GREEN
                }
            )
        }

        return true
    }
}
