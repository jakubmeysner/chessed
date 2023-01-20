package com.jakubmeysner.chessed.commands

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import kotlin.math.pow

class ArenaBuildCommand(private val plugin: Chessed) : TabExecutor {
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

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.size != 1) {
            sender.spigot().sendMessage(
                TextComponent("Usage: /arena build <arena>").apply {
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
            val arena = plugin.arenas.getValue(args[0])

            sender.spigot().sendMessage(
                TextComponent("Building arena ${arena.name}...")
            )

            for (x in 0..<40) {
                for (y in 0..<40) {
                    val block = arena.getBlock(x, y, -1)

                    block.type =
                        if ((-1.0).pow(1.0 * ((x / 5) + (y / 5))) == 1.0)
                            Material.BLACK_CONCRETE
                        else
                            Material.WHITE_CONCRETE
                }
            }

            sender.spigot().sendMessage(
                TextComponent("Built arena ${arena.name}.").apply {
                    color = ChatColor.GREEN
                }
            )
        }

        return true
    }
}
