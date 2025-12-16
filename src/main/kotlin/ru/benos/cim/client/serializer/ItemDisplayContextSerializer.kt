package ru.benos.cim.client.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.world.item.ItemDisplayContext

object ItemDisplayContextSerializer : KSerializer<ItemDisplayContext> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.minecraft.world.item.ItemDisplayContext", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ItemDisplayContext) =
        encoder.encodeString(value.serializedName)

    override fun deserialize(decoder: Decoder): ItemDisplayContext {
        val stringKey = decoder.decodeString()

        // Пытаемся найти
        val found = ItemDisplayContext.entries.find { it.serializedName == stringKey }

        return found ?: ItemDisplayContext.NONE
    }
}