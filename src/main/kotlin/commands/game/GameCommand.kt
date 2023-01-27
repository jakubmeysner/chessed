package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.commands.ParentCommand

class GameCommand(private val plugin: Chessed) : ParentCommand(
    "game",
    mapOf(
        "resign" to GameResignCommand(plugin)
    )
)
