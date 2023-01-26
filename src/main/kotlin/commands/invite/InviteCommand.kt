package com.jakubmeysner.chessed.commands.invite

import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.commands.ParentCommand

class InviteCommand(private val plugin: Chessed) : ParentCommand(
    "invite",
    mapOf(
        "decline" to InviteDeclineCommand(plugin),
        "cancel" to InviteCancelCommand(plugin)
    )
)
