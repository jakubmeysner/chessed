@file:UseSerializers(LocationSerializer::class)

package com.jakubmeysner.chessed.models

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import com.jakubmeysner.chessed.Chessed
import com.jakubmeysner.chessed.serializers.LocationSerializer
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

@Serializable
class Arena(
    val name: String,
    val location: Location
) {
    val piecesClipboards = mutableMapOf<Piece, Clipboard>()
    val pieces = mutableMapOf<Square, Piece>()

    fun getBlock(x: Int, y: Int, z: Int): Block {
        return location.block.getRelative(
            when (location.yaw) {
                90F -> -y
                0F -> -x
                -90F -> y
                -180F -> x
                else -> throw IllegalStateException()
            },
            when (location.yaw) {
                90F -> -x
                0F -> y
                -90F -> x
                -180F -> -y
                else -> throw IllegalStateException()
            },
            z
        )
    }

    fun buildPiece(square: Square, piece: Piece) {
        if (pieces[square] == piece) {
            return
        }

        val block = getBlock(
            square.file.ordinal * 7,
            0,
            square.rank.ordinal * 7
        )

        val clipboard = piecesClipboards.computeIfAbsent(piece) {
            Chessed.instance.getResource("schematics/${piece.name}.schem").use {
                BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(it).read()
            }
        }

        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.world)).use { session ->
            val operation = ClipboardHolder(clipboard)
                .createPaste(session)
                .to(BlockVector3.at(block.x, block.y, block.z))
                .build()

            Operations.complete(operation)
        }

        pieces[square] = piece
    }

    companion object {
        const val squareSide = 7
        val whiteSquareMaterial = Material.WHITE_CONCRETE
        val blackSquareMaterial = Material.BLACK_CONCRETE
    }
}
