package ru.benos.cim.client.serializable

import kotlinx.serialization.Serializable

@Serializable
data class CIMDisplayProfileData(
    val model: String       = "./model.geo.json",
    val texture: String     = "./texture.png",
    val animations: String? = "./animations.json",
    val display: String?    = "./display.json"
)
