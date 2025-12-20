package ru.benos.cim.client
//? if fabric {
//?}
import kotlinx.serialization.json.*
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import ru.benos.cim.client.CIM.rl
import ru.benos.cim.client.CIMModelsRegistry.registry
import ru.benos.cim.client.exception.CIMException
import ru.benos.cim.client.exception.CIMExceptionType
import ru.benos.cim.client.geo.CIMItemRenderer
import ru.benos.cim.client.serializable.CIMModelsData
import ru.benos.cim.client.serializable.CIMModelsEntry

object CIMReload {
    val JSON: Json = Json { ignoreUnknownKeys = true }
    const val CIM_MODELS: String = "cim_models"
    const val PROPERTIES: String = "properties"

    //? if fabric {
    object ReloadFabric: SimpleSynchronousResourceReloadListener {
        override fun getFabricId(): ResourceLocation = "${CIM.MODID}:resources".rl
        override fun onResourceManagerReload(resourceManager: ResourceManager) = reload(resourceManager)
    }
    //?}

    private fun reload(manager: ResourceManager) {
        CIMModelsRegistry.clearAll()
        CIMItemRenderer.clearCache()

        // Уведомление о начале загрузки //
        CIM.LOGGER.info("#Уведомление об начале загрузки моделей CIM.")

        // Проходка по namespace //
        manager.namespaces.forEach { namespace ->
            // Получение ресурса 'cim_models.json' //
            val cimModelsLocation = "$namespace:$CIM_MODELS.json"
            val cimModelsRes = manager.getResource(cimModelsLocation.rl)

            val cimModelsDirLocation = "$namespace:$CIM_MODELS"
            val cimModelDirRes = manager.getResource(cimModelsDirLocation.rl)

            // Проверка на наличие 'cim_models.json' //
            if (cimModelsRes.isEmpty) {
                if (cimModelDirRes.isPresent)
                    CIMException(
                        type = CIMExceptionType.Location.DirExistButFileNot,
                        args = arrayOf(cimModelsLocation)
                    )
                return@forEach
            }

            // Проверка на наличие директории 'cim_models' //
            if (cimModelDirRes.isEmpty) {
                CIMException(
                    type = CIMExceptionType.Location.FileExistButFirNot,
                    args = arrayOf(cimModelsLocation)
                )
                return@forEach
            }

            val cimModels = cimModelsRes.get()
            val cimModelsJsonElement = cimModels.jsonElement

            // Проверка на корректность структуры файла 'cim_models.json' //
            // Существует ли поле 'entries'? //
            if (!cimModelsJsonElement.contain("entries")) {
                CIMException(
                    type = CIMExceptionType.File.CimModelsJson.Entries.IsNotExist,
                    args = arrayOf(cimModelsLocation)
                )
                return@forEach
            }

            // Поле 'entries' - это массив? //
            if (cimModelsJsonElement.jsonObject["entries"] !is JsonArray) {
                CIMException(
                    type = CIMExceptionType.File.CimModelsJson.Entries.IsNotArray,
                    args = arrayOf(cimModelsLocation)
                )
                return@forEach
            }

            // Поле 'entries' - не пустое? //
            if ( (cimModelsJsonElement.jsonObject["entries"] as JsonArray).isEmpty() ) {
                CIMException(
                    type = CIMExceptionType.File.CimModelsJson.Entries.IsEmpty,
                    args = arrayOf(cimModelsLocation)
                )
                return@forEach
            }

            try {
                // Парсинг JSON -> object //
                JSON.decodeFromJsonElement<CIMModelsData>(cimModelsJsonElement)
                    .registry(manager)
            } catch (e: Exception) {
                CIMException(
                    type = CIMExceptionType.Unknown,
                    exception = e
                )
                return@forEach
            }
        }

        CIM.LOGGER.flush()
    }

    val Resource.jsonElement: JsonElement
        get() = JSON.parseToJsonElement( openAsReader().use { it.readText() } )

    fun JsonElement.contain(key: String): Boolean = key in this.jsonObject
}