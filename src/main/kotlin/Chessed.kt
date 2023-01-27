package com.jakubmeysner.chessed

import com.jakubmeysner.chessed.commands.arena.ArenaCommand
import com.jakubmeysner.chessed.commands.invite.InviteCommand
import com.jakubmeysner.chessed.commands.play.PlayCommand
import com.jakubmeysner.chessed.models.Arena
import com.jakubmeysner.chessed.models.Game
import com.jakubmeysner.chessed.models.Invite
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

@Suppress("unused")
class Chessed : JavaPlugin() {
    val arenas = mutableMapOf<String, Arena>()
    val invites = mutableMapOf<Player, Invite>()
    val games = mutableListOf<Game>()

    private val arenasFile = dataFolder.resolve("arenas.json")

    private val commands = mapOf(
        "play" to PlayCommand(this),
        "invite" to InviteCommand(this),
        "arena" to ArenaCommand(this)
    )

    override fun onEnable() {
        if (!dataFolder.isDirectory) {
            dataFolder.mkdir()
        }

        if (arenasFile.exists()) {
            Json.decodeFromString<List<Arena>>(arenasFile.readText()).forEach {
                arenas[it.name] = it
            }
        }

        commands.forEach { (name, executor) ->
            getCommand(name)?.setExecutor(executor)
                ?: throw Error("Command not registered")
        }
    }

    override fun onDisable() {
        if (!dataFolder.isDirectory) {
            dataFolder.mkdir()
        }

        arenasFile.writeText(Json.encodeToString(arenas.values))
    }

    fun runTaskLater(delay: Long, runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay)
    }
}
