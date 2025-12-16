package ru.benos.cim.client.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CIMComponentMode {
    @SerialName("any") ANY,
    @SerialName("all") ALL,
    @SerialName("only") ONLY
}