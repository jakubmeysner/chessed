package com.jakubmeysner.chessed.commands.arena

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.models.Arena
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ArenaBuildCommand(private val plugin: Chessed) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when (args.lastIndex) {
            0 -> plugin.arenas.keys.toList()
            else -> emptyList()
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

            return true
        }

        val arena = plugin.arenas[args[0]] ?: let {
            sender.spigot().sendMessage(
                TextComponent("Argument <arena> must be an existing arena name!").apply {
                    color = ChatColor.RED
                }
            )

            return true
        }

        sender.spigot().sendMessage(
            TextComponent("Building arena ${arena.name}...")
        )

        for (x in 0..<(Arena.squareSide * 8)) {
            for (z in 0..<(Arena.squareSide * 8)) {
                val block = arena.getBlock(x, -1, z)

                block.type =
                    if ((x / Arena.squareSide + z / Arena.squareSide) % 2 == 0) Arena.blackSquareMaterial
                    else Arena.whiteSquareMaterial
            }
        }

        sender.spigot().sendMessage(
            TextComponent("Built arena ${arena.name}.").apply {
                color = ChatColor.GREEN
            }
        )

        return true
    }
}
