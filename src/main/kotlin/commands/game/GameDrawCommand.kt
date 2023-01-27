package com.jakubmeysner.chessed.commands.game

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.commands.ParentCommand

class GameDrawCommand(private val plugin: Chessed) : ParentCommand(
    "game draw",
    mapOf(
        "offer" to GameDrawOfferCommand(plugin),
        "accept" to GameDrawAcceptCommand(plugin),
        "decline" to GameDrawDeclineCommand(plugin)
    )
)
