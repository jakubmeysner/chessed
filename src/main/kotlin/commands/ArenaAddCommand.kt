package com.jakubmeysner.chessed.commands

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Arena
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
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
        return when (args.size) {
            2 -> Bukkit.getWorlds().map { it.name }

            in 2..5 -> {
                if (sender is Entity) {
                    return listOf(sender.location.let {
                        when (args.size) {
                            3 -> it.blockX
                            4 -> it.blockY
                            5 -> it.blockZ
                            else -> -1
                        }
                    }.toString())
                } else {
                    return listOf()
                }
            }

            6 -> listOf("0", "90", "180", "270")
            else -> listOf()
        }.filter { it.startsWith(args.last()) }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Entity && args.size !in 1..6) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Usage: /arena add <arena> [<worldName>] [<x>] [<y>] [<z>] [<yaw>]")
                    .color(ChatColor.RED).create()
            )
        } else if (sender !is Entity && args.size != 6) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Usage: /arena add <arena> <worldName> <x> <y> <z> <yaw>")
                    .color(ChatColor.RED).create()
            )
        } else if (!args[0].matches(Regex("""^\w{3,16}$"""))) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <arena> must contain 3-16 alphanumeric characters and underscores!")
                    .color(ChatColor.RED).create()
            )
        } else if (args.size >= 3 && args[2].toIntOrNull() == null) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <x> must be an integer!")
                    .color(ChatColor.RED).create()
            )
        } else if (args.size >= 4 && args[3].toIntOrNull() == null) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <y> must be an integer!")
                    .color(ChatColor.RED).create()
            )
        } else if (args.size >= 5 && args[4].toIntOrNull() == null) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <z> must be an integer!")
                    .color(ChatColor.RED).create()
            )
        } else if (args.size >= 6 && args[5] !in listOf("0", "90", "180", "270")) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <yaw> must be 0, 90, 180 or 270!")
                    .color(ChatColor.RED).create()
            )
        } else if (args[0] in plugin.arenas.keys) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <arena> must be a unique arena name!")
                    .color(ChatColor.RED).create()
            )
        } else if (args.size >= 2 && Bukkit.getWorld(args[1]) == null) {
            sender.spigot().sendMessage(
                *ComponentBuilder("Argument <worldName> must be a world name!")
                    .color(ChatColor.RED).create()
            )
        } else {
            val name = args[0]

            val world = args.getOrNull(1)?.let { Bukkit.getWorld(it) } ?: (sender as Entity).location.world
            val x = args.getOrNull(2)?.toInt() ?: (sender as Entity).location.blockX
            val y = args.getOrNull(3)?.toInt() ?: (sender as Entity).location.blockY
            val z = args.getOrNull(4)?.toInt() ?: (sender as Entity).location.blockZ
            val yaw = args.getOrNull(5)?.toInt() ?: ((sender as Entity).location.yaw.toInt() / 90 * 90)

            val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble(), yaw.toFloat(), 0F)

            val arena = Arena(name, location)
            plugin.arenas[arena.name] = arena

            sender.spigot().sendMessage(
                *ComponentBuilder("Created new arena ${arena.name}.")
                    .color(ChatColor.GREEN).create()
            )
        }

        return true
    }
}
