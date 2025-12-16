package ru.benos.cim.client.serializable

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CIMModelsData(val entries: List<CIMModelsEntry>)

@Serializable
data class CIMModelsEntry(
    val items: List<String> = listOf(),
    val components: JsonElement? = null,
    val mode: CIMComponentMode = CIMComponentMode.ANY,
    val priority: Int = 0,
    val model: String
)
