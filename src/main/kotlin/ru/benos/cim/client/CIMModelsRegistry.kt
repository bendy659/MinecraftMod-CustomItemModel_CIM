package ru.benos.cim.client

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Vector3f
import ru.benos.cim.client.CIM.fullLocation
import ru.benos.cim.client.CIM.localLocation
import ru.benos.cim.client.CIM.rl
import ru.benos.cim.client.CIM.translate
import ru.benos.cim.client.CIMReload.jsonElement
import ru.benos.cim.client.exception.CIMException
import ru.benos.cim.client.exception.CIMExceptionType
import ru.benos.cim.client.serializable.CIMComponentMode
import ru.benos.cim.client.serializable.CIMDisplayProfileData
import ru.benos.cim.client.serializable.CIMModelsData
import ru.benos.cim.client.serializable.CIMPropertiesData
import ru.benos.cim.client.serializable.DisplayData
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.loading.json.raw.Model
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter
import software.bernie.geckolib.loading.`object`.BakedAnimations
import software.bernie.geckolib.loading.`object`.BakedModelFactory
import software.bernie.geckolib.loading.`object`.GeometryTree
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

object CIMModelsRegistry {
    val ERROR_MODEL_ID = "cim:error"
    val ERROR_MODEL_LOCATION = "cim:cim_models/error/model.geo.json"
    val ERROR_TEXTURE_LOCATION = "cim:cim_models/error/texture.png"
    val ERROR_ANIMATIONS_LOCATION = "cim:cim_models/error/animations.json"

    val EMPTY_MODEL_ID = "cim:empty"

    private val ignoreModelError = listOf(ERROR_MODEL_ID, EMPTY_MODEL_ID)

    // Key: ResourceLocation = Based <ItemID>
    private val REGISTRY: MutableMap<ResourceLocation, RegistryValue> = ConcurrentHashMap()
    data class RegistryValue(
        var components: DataComponentPatch? = null,
        val mode: CIMComponentMode,
        val priority: Int = 0,
        val properties: CIMPropertiesData,
        val modelId: ResourceLocation
    )

    private val BAKED: MutableMap<BakedKey, BakedValue> = ConcurrentHashMap()
    private data class BakedKey(val modelId: ResourceLocation, val itemDisplayContext: ItemDisplayContext)
    private data class BakedValue(val model: BakedGeoModel, val animations: BakedAnimations?)

    private val DISPLAY_TRANSFORMS: MutableMap<ResourceLocation, ItemTransform> = ConcurrentHashMap()

    fun clearAll() { REGISTRY.clear(); BAKED.clear(); DISPLAY_TRANSFORMS.clear() }

    fun getModelId(itemId: ResourceLocation): ResourceLocation =
        REGISTRY[itemId]?.modelId
            ?: ERROR_MODEL_ID.rl

    fun getModelLocation(itemId: ResourceLocation, itemDisplayContext: ItemDisplayContext): String =
        REGISTRY[itemId]?.properties?.displayContext[itemDisplayContext]?.model
            ?: REGISTRY[itemId]?.properties?.displayContext[ItemDisplayContext.NONE]?.model
            ?: ERROR_MODEL_LOCATION

    fun getTextureLocation(itemId: ResourceLocation, itemDisplayContext: ItemDisplayContext): String =
        REGISTRY[itemId]?.properties?.displayContext[itemDisplayContext]?.texture
            ?: REGISTRY[itemId]?.properties?.displayContext[ItemDisplayContext.NONE]?.texture
            ?: ERROR_TEXTURE_LOCATION

    fun getAnimationsLocation(itemId: ResourceLocation, itemDisplayContext: ItemDisplayContext): String =
        REGISTRY[itemId]?.properties?.displayContext[itemDisplayContext]?.animations
            ?: REGISTRY[itemId]?.properties?.displayContext[ItemDisplayContext.NONE]?.animations
            ?: ERROR_ANIMATIONS_LOCATION

    fun getBakedModel(modelId: ResourceLocation, itemDisplayContext: ItemDisplayContext): BakedGeoModel? {
        val keyExact = BakedKey(modelId, itemDisplayContext)
        val keyDefault = BakedKey(modelId, ItemDisplayContext.NONE)

        val exact = BAKED[keyExact]?.model
        if (exact != null) return exact

        val default = BAKED[keyDefault]?.model
        if (default != null) return default

        // Fallback to error...
        return BAKED[BakedKey(ERROR_MODEL_ID.rl, ItemDisplayContext.NONE)]?.model
    }

    fun getBakedAnimations(modelId: ResourceLocation, itemDisplayContext: ItemDisplayContext): BakedAnimations? {
        val keyExact = BakedKey(modelId, itemDisplayContext)

        val exact = BAKED[keyExact]?.animations
        if (exact != null) return exact

        return BAKED[BakedKey(ERROR_MODEL_ID.rl, ItemDisplayContext.NONE)]?.animations
    }

    fun getDisplayTransform(modelId: String, displayContext: ItemDisplayContext): ItemTransform {
        val key = "$modelId:${displayContext.serializedName}".rl

        val exactKey = DISPLAY_TRANSFORMS[key]
        if (exactKey == null) DISPLAY_TRANSFORMS.computeIfAbsent(key) { ItemTransform.NO_TRANSFORM }

        return exactKey ?: ItemTransform.NO_TRANSFORM
    }

    fun getModelData(itemId: ResourceLocation): RegistryValue? =
        REGISTRY[itemId]

    fun CIMModelsData.registry(manager: ResourceManager) {
        // Проходка по списку //
        entries.forEach { (items, components, mode, priority, model) ->
            if (model in ignoreModelError) CIMException.muteNext(CIMExceptionType.File.Unknown)

            try {
                val componentsFinal = components.parseToDataComponentPatch
                val propertiesFinal = model.parseToCIMPropertiesData(manager)

                // Запекание моделей и анимаций //
                propertiesFinal.displayContext.forEach { (displayContext, profile) ->
                    val key = BakedKey(model.rl, displayContext)
                    val value = baking(manager, profile.model.rl, profile.animations?.rl)
                    BAKED[key] = value

                    // Парсинг трансформов //
                    if (profile.display != null)
                        parseDisplayTransforms(manager, profile.display.rl)
                }

                // Регистрация моделей по itemID //
                items.forEach { itemId ->
                    REGISTRY[itemId.rl] = RegistryValue(componentsFinal, mode, priority, propertiesFinal, model.rl) }
            } catch (e: Exception) {
                CIMException(
                    type = CIMExceptionType.File.Unknown,
                    exception = e
                )
            }
        }
    }

    fun parseDisplayTransforms(manager: ResourceManager, location: ResourceLocation?) {
        location ?: return

        val modelId = location.toString()
            .replaceBefore("cim_models/", "")
            .replace("cim_models", "")
            .replaceAfterLast("/", "")
            .replace("/", "")

        val resource = manager.getResource(location)
        if (resource.isEmpty) return

        val jsonElement = resource.get().jsonElement
        val displayData = CIMReload.JSON.decodeFromJsonElement<DisplayData>(jsonElement)
        displayData.display.forEach { (context, transform) ->
            val contextSerialName = context.serializedName
            val finalTransform = if (
                contextSerialName.contains("hand", true) ||
                contextSerialName.contains("gui", true) ||
                contextSerialName.contains("fixed", true) ||
                contextSerialName.contains("ground", true)
            ) ItemTransform(
                transform.itemTransform.rotation,
                Vector3f(
                    transform.itemTransform.translation.x / 16f,
                    transform.itemTransform.translation.y / 16f,
                    transform.itemTransform.translation.z / 16f
                ),
                transform.itemTransform.scale
            )
            else transform.itemTransform

            DISPLAY_TRANSFORMS["$modelId:${context.serializedName}".rl] = finalTransform
        }
    }

    private val JsonElement?.parseToDataComponentPatch: DataComponentPatch? get() {
        if (this == null) return null

        // 1. Конвертируем в Gson
        val rawJson = this.toString()
        val gsonElement = try {
            JsonParser.parseString(rawJson)
        } catch (e: Exception) {
            CIMException(CIMExceptionType.Unknown, exception = e)
            return null
        }

        if (!gsonElement.isJsonObject) {
            CIMException(CIMExceptionType.File.CimModelsJson.Components.IsNotObject)
            return null
        }

        val jsonObject = gsonElement.asJsonObject
        val builder = DataComponentPatch.builder()

        // Идем по каждому ключу вручную
        for ((key, value) in jsonObject.entrySet()) {
            try {
                // Пытаемся понять, что это за компонент (например, "minecraft:enchantments")
                val location = ResourceLocation.tryParse(key)

                val type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(location!!)

                // Пытаемся распарсить значение с помощью кодека этого конкретного компонента
                val parseResult = type!!.codecOrThrow().parse(JsonOps.INSTANCE, value)

                val componentValue = parseResult.getOrThrow { errorMsg ->
                    IllegalStateException(errorMsg) // Кидаем ошибку, чтобы поймать ниже
                }

                @Suppress("UNCHECKED_CAST")
                fun <T> setComponentSafe(builder: DataComponentPatch.Builder, type: DataComponentType<T>, value: Any) =
                    builder.set(type, value as T)

                // В. Добавляем в билдер (используем хак с capture, чтобы типы сошлись)
                setComponentSafe(builder, type, componentValue)

            } catch (e: Exception) {
                CIMException(
                    type = CIMExceptionType.File.CimModelsJson.Components.UnknownComponent,
                    exception = e,
                    args = arrayOf(key)
                )

                return null
            }
        }

        return builder.build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun String.parseToCIMPropertiesData(manager: ResourceManager): CIMPropertiesData {
        // Полный путь к файлу //
        val fullLocation = "${this.fullLocation}/${CIMReload.PROPERTIES}.json"
        val splits = fullLocation.split(':')
        val namespace = splits[0]
        val modelId = splits[1]
            .removePrefix("cim_models/")
            .removeSuffix("/${CIMReload.PROPERTIES}.json")

        // Получение ресурса //
        val propertiesRes = manager.getResource(fullLocation.rl)

        // Генерация //
        val pName = fullLocation.replace("/${CIMReload.PROPERTIES}.json", uniqueID)
        val pAuthors = listOf( "cim.unknown".translate.string )

        try {
            val propertiesJsonElement = propertiesRes.get().jsonElement
            val propertiesObject = CIMReload.JSON.decodeFromJsonElement<CIMPropertiesData>(propertiesJsonElement)

            val displayContext = mutableMapOf<ItemDisplayContext, CIMDisplayProfileData>()
            propertiesObject.displayContext.forEach { (key, value) ->
                val fullValue = value.fullLocations(namespace, modelId) ?: return@forEach
                displayContext += key to fullValue
            }

            return CIMPropertiesData(
                name = propertiesObject.name ?: pName,
                authors = propertiesObject.authors ?: pAuthors,
                disableGroundBobbing = propertiesObject.disableGroundBobbing,
                disableGroundSpinning = propertiesObject.disableGroundSpinning,
                displayContext = displayContext
            )
        } catch (e: Exception) {
            CIMException(
                type = CIMExceptionType.File.Unknown,
                exception = e,
                args = arrayOf(
                    fullLocation.substringAfterLast("/"),
                    fullLocation.substringBeforeLast("/")
                )
            )

            // Ресурс не был обнаружен. Создаём новый runtime-properties.json //
            val displayContext = mutableMapOf<ItemDisplayContext, CIMDisplayProfileData>()
            CIMPropertiesData().displayContext.forEach { (key, value) ->
                val fullValue = value.fullLocations(namespace, modelId) ?: return@forEach
                displayContext += key to fullValue
            }

            return CIMPropertiesData(
                name = pName,
                authors = pAuthors,
                displayContext = displayContext
            )
        }
    }

    private val uniqueID: String get() {
        val formatted = DateTimeFormatter.ofPattern("SSS_ss_mm_HH/dd_MM_yyyy")
        val dat = LocalDateTime.now().format(formatted)
        val rnd = Random.nextInt()
        val build = "-runtime-$dat-$rnd"

        return build
    }

    fun CIMDisplayProfileData.fullLocations(namespace: String, modelId: String): CIMDisplayProfileData? {
        fun String?.normalizeLocation(): String? =
            this@normalizeLocation
                ?.localLocation(namespace)
                ?.fullLocation
                ?.replace("cim_models/", "cim_models/$modelId/")

        val modelLocation = this.model.normalizeLocation()
        val textureLocation = this.texture.normalizeLocation()
        val animationsLocation = this.animations.normalizeLocation()
        val displayLocation = this.display.normalizeLocation()

        if (modelLocation == null) {
            CIMException(type = CIMExceptionType.File.PropertiesJson.Require.Model)
            return null
        } else if (textureLocation == null) {
            CIMException(type = CIMExceptionType.File.PropertiesJson.Require.Texture)
            return null
        }

        return CIMDisplayProfileData(modelLocation, textureLocation, animationsLocation, displayLocation)
    }

    private fun baking(manager: ResourceManager, modelLocation: ResourceLocation?, animationsLocation: ResourceLocation?): BakedValue {
        var bakedModel: BakedGeoModel? = null
        var bakedAnimations: BakedAnimations? = null

        // Запекание модели //
        if (modelLocation != null) {
            val modelRes = manager.getResource(modelLocation)
            if (modelRes.isEmpty) {
                throw CIMException(
                    type = CIMExceptionType.File.NotExist,
                    args = arrayOf(modelLocation.path, modelLocation)
                )
            }
            modelRes.get()
                .open()
                .use { inputStream ->
                    val jsonObject = JsonParser.parseReader(InputStreamReader(inputStream))
                        .asJsonObject

                    val raw = KeyFramesAdapter.GEO_GSON
                        .fromJson(jsonObject, Model::class.java)

                    val geoTree = GeometryTree.fromModel(raw)
                    bakedModel = BakedModelFactory.getForNamespace(modelLocation.namespace)
                        .constructGeoModel(geoTree)
                }
        }

        // Запекание анимаций //
        if (animationsLocation != null) {
            val animationsRes = manager.getResource(animationsLocation)
            if (animationsRes.isPresent)
                animationsRes.get()
                    .open()
                    .use { inputStream ->
                        val jsonObject = JsonParser.parseReader(InputStreamReader(inputStream))
                            .asJsonObject["animations"]

                        bakedAnimations = KeyFramesAdapter.GEO_GSON
                            .fromJson(jsonObject, BakedAnimations::class.java)
                    }
        }

        bakedModel ?: throw CIMException(CIMExceptionType.Custom("Error by baking model for '$modelLocation'. BakedGeoModel is null!"))
        //bakedAnimations ?: throw CIMException(CIMExceptionType.Custom("Error by baking animations for '$animationsLocation'. BakedAnimations is null!"))

        return BakedValue(bakedModel, bakedAnimations)
    }
}