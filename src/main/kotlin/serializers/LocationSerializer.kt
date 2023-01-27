package com.jakubmeysner.chessed.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

object LocationSerializer : KSerializer<Location> {
    override val descriptor = LocationSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Location {
        val surrogate = decoder.decodeSerializableValue(
            LocationSurrogate.serializer()
        )

        return Location(
            Bukkit.getWorld(UUID.fromString(surrogate.worldId)),
            surrogate.x,
            surrogate.y,
            surrogate.z,
            surrogate.yaw,
            surrogate.pitch
        )
    }

    override fun serialize(encoder: Encoder, value: Location) {
        val surrogate = LocationSurrogate(
            value.world?.uid.toString(),
            value.x,
            value.y,
            value.z,
            value.yaw,
            value.pitch
        )

        encoder.encodeSerializableValue(
            LocationSurrogate.serializer(),
            surrogate
        )
    }
}
