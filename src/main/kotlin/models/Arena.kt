package com.jakubmeysner.chessed.models

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

class Arena(val name: String, val location: Location) {
    fun getBlock(x: Int, y: Int, z: Int): Block {
        return location.block.getRelative(
            when (location.yaw) {
                90F -> -y
                0F -> -x
                -90F -> y
                -180F -> x
                else -> throw IllegalStateException()
            },
            z,
            when (location.yaw) {
                90F -> -x
                0F -> y
                -90F -> x
                -180F -> -y
                else -> throw IllegalStateException()
            }
        )
    }

    companion object {
        const val squareSide = 7
        val whiteSquareMaterial = Material.WHITE_CONCRETE
        val blackSquareMaterial = Material.BLACK_CONCRETE
    }
}
