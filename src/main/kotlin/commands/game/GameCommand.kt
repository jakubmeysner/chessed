package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.commands.ParentCommand

class GameCommand(private val plugin: Chessed) : ParentCommand(
    "game",
    mapOf(
        "move" to GameMoveCommand(plugin),
        "menu" to GameMenuCommand(plugin),
        "history" to GameHistoryCommand(plugin),
        "draw" to GameDrawCommand(plugin),
        "resign" to GameResignCommand(plugin)
    )
)
