package ru.benos.cim.client.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.item.ItemDisplayContext

@Serializable
data class CIMPropertiesData(
    val name: String? = null,
    val authors: List<String>? = null,
    @SerialName("disable_ground_bobbing") val disableGroundBobbing: Boolean = false,
    @SerialName("disable_ground_spinning") val disableGroundSpinning: Boolean = false,
    @SerialName("display_context") val displayContext: Map<ItemDisplayContext, CIMDisplayProfileData> = mapOf(ItemDisplayContext.NONE to CIMDisplayProfileData())
)