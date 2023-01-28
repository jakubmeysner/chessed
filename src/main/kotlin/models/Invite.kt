package com.jakubmeysner.chessed.models

import com.jakubmeysner.chessed.Chessed
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import kotlin.random.Random

class Invite(
    private val plugin: Chessed,
    val inviter: Player,
    val invitee: Player,
    val time: Game.Time,
    val playAsWhite: Boolean?
) {
    val autoCancelTask = plugin.runTaskLater(autoCancelDelay) {
        cancel(true)

        inviter.spigot().sendMessage(
            TextComponent("The invite that you sent to ${invitee.name} has expired.").apply {
                color = ChatColor.YELLOW
            }
        )

        invitee.spigot().sendMessage(
            TextComponent("The invite that you've received from ${inviter.name} has expired.").apply {
                color = ChatColor.YELLOW
            }
        )
    }

    init {
        plugin.invites[inviter] = this

        inviter.spigot().sendMessage(
            TextComponent("You've invited ${invitee.name} to a game.").apply {
                color = ChatColor.GREEN
            }
        )

        invitee.spigot().sendMessage(
            TextComponent("You've been invited to a game by ${inviter.name}.\n").apply {
                color = ChatColor.AQUA
            },
            TextComponent("[Accept]").apply {
                color = ChatColor.GREEN
                isBold = true

                clickEvent = ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/invite accept ${inviter.name}"
                )
            },
            TextComponent(" "),
            TextComponent("[Decline]").apply {
                color = ChatColor.RED
                isBold = true

                clickEvent = ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/invite decline ${inviter.name}"
                )
            }
        )
    }

    fun accept() {
        val arena = plugin.arenas.values
            .find { arena -> plugin.games.none { it.arena == arena } }
            ?: let {
                invitee.spigot().sendMessage(
                    TextComponent("No arenas are currently available.").apply {
                        color = ChatColor.RED
                    }
                )

                return
            }

        cancel()

        val whitePlayer = if (
            playAsWhite == true || (playAsWhite == null && Random.nextBoolean())
        ) inviter else invitee

        Game(
            plugin,
            arena,
            whitePlayer,
            if (inviter == whitePlayer) invitee else inviter,
            time
        )
    }

    fun decline() {
        cancel()

        inviter.spigot().sendMessage(
            TextComponent("Your invite to a game was declined by ${invitee.name}.").apply {
                color = ChatColor.YELLOW
            }
        )

        invitee.spigot().sendMessage(
            TextComponent("You've declined ${inviter.name}'s invite to a game.").apply {
                color = ChatColor.YELLOW
            }
        )
    }

    fun cancel(auto: Boolean = false) {
        if (!auto) {
            autoCancelTask.cancel()
        } else {
            invitee.spigot().sendMessage(
                TextComponent("The invite that you received from ${inviter.name} has been canceled.").apply {
                    color = ChatColor.YELLOW
                }
            )
        }

        plugin.invites.remove(inviter)
    }

    companion object {
        const val autoCancelDelay = 60 * 20L
    }
}
