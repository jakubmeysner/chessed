package com.jakubmeysner.chessed

import com.jakubmeysner.chessed.commands.ArenaCommand
import com.jakubmeysner.chessed.models.Arena
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Chessed : JavaPlugin() {
    val arenas = mutableMapOf<String, Arena>()

    override fun onEnable() {
        getCommand("arena")?.setExecutor(ArenaCommand(this))
            ?: throw Error("Command not registered")
    }
}
