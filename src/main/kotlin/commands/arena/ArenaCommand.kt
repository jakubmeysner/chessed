package com.jakubmeysner.chessed.commands.arena

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.commands.ParentCommand

class ArenaCommand(private val plugin: Chessed) : ParentCommand(
    "arena",
    mapOf(
        "add" to ArenaAddCommand(plugin),
        "build" to ArenaBuildCommand(plugin),
        "remove" to ArenaRemoveCommand(plugin)
    )
)
