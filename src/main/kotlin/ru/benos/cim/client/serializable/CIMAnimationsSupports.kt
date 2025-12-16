package ru.benos.cim.client.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CIMAnimationsSupports {
    // Для всех остальных случаев. Анимация по умолчанию
    @SerialName("all") ALL,
}