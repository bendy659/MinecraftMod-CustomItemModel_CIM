package ru.benos.cim.client.serializable

import kotlinx.serialization.Serializable
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Vector3f
import ru.benos.cim.client.serializer.ItemDisplayContextSerializer

@Serializable
data class DisplayData(val display: Map<@Serializable(ItemDisplayContextSerializer::class) ItemDisplayContext, DisplayTransform>)

@Serializable
data class DisplayTransform(
    val translation: FloatArray = floatArrayOf(0f, 0f, 0f),
    val rotation: FloatArray = floatArrayOf(0f, 0f, 0f),
    val scale: FloatArray = floatArrayOf(1f, 1f, 1f)
) {
    val itemTransform: ItemTransform get() =
        ItemTransform(this.rotation.vector3f, this.translation.vector3f, this.scale.vector3f)

    val FloatArray.vector3f: Vector3f get() =
        Vector3f(this@vector3f[0], this@vector3f[1], this@vector3f[2])

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayTransform

        if (!translation.contentEquals(other.translation)) return false
        if (!rotation.contentEquals(other.rotation)) return false
        if (!scale.contentEquals(other.scale)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = translation.contentHashCode()
        result = 31 * result + rotation.contentHashCode()
        result = 31 * result + scale.contentHashCode()
        return result
    }
}