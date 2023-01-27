package com.jakubmeysner.chessed

import com.jakubmeysner.chessed.commands.arena.ArenaCommand
import com.jakubmeysner.chessed.commands.game.GameCommand
import com.jakubmeysner.chessed.commands.invite.InviteCommand
import com.jakubmeysner.chessed.commands.play.PlayCommand
import com.jakubmeysner.chessed.listeners.PlayerInteractListener
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

    private val commands = mapOf(
        "play" to PlayCommand(this),
        "invite" to InviteCommand(this),
        "game" to GameCommand(this),
        "arena" to ArenaCommand(this)
    )

    private val listeners = listOf(
        PlayerInteractListener(this)
    )

    private val arenasDataFile = dataFolder.resolve("arenas.json")

    override fun onEnable() {
        readData()

        commands.forEach { (name, executor) ->
            getCommand(name)?.setExecutor(executor)
                ?: throw Error("Command not registered")
        }

        listeners.forEach {
            server.pluginManager.registerEvents(it, this)
        }
    }

    override fun onDisable() {
        writeData()
    }

    fun runTaskLater(delay: Long, runnable: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay)
    }

    private fun readData() {
        if (!dataFolder.isDirectory) {
            dataFolder.mkdir()
        }

        if (arenasDataFile.isFile) {
            Json.decodeFromString<List<Arena>>(arenasDataFile.readText()).forEach {
                arenas[it.name] = it
            }
        }
    }

    private fun writeData() {
        if (!dataFolder.isDirectory) {
            dataFolder.mkdir()
        }

        arenasDataFile.writeText(Json.encodeToString(arenas.values.toList()))
    }
}
