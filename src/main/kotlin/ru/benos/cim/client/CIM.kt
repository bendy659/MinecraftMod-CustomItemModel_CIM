package ru.benos.cim.client

//? if fabric {
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.ModContainer
//? }
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.world.phys.Vec3
import ru.benos.cim.client.patch.MainMenuPatch
import java.util.*

interface IModLoader {
    val loader: String
    val isLoaded: Boolean
    val modContainer: Optional<ModContainer>
}

@Environment(EnvType.CLIENT)
object CIM {
    lateinit var LOADER: IModLoader

    const val MODID: String = "cim"
    const val CIM_MODELS: String = "cim_models"

    val LOGGER: CIMLogger = CIMLogger(MODID.uppercase())
    val MC: Minecraft by lazy { Minecraft.getInstance() }

    val DYNAMIC_COMPONENTS: MutableList<String> = mutableListOf()

    fun launch(pLoader: IModLoader) {
        LOADER = pLoader

        LOGGER.info("Mod launch in ${LOADER.loader} loader!")

        //? if fabric {
        // Подключение перезагрузки ресурсов //
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(CIMReload.ReloadFabric)

        // Регистрация ресурс пака //
        ResourceManagerHelper
            .registerBuiltinResourcePack(
                "cim:cim_example_models".rl,
                LOADER.modContainer.get(),
                "(CIM) Example models".literal,
                ResourcePackActivationType.NORMAL
            )
        //? }

        // Применение патчей //
        MainMenuPatch.apply
    }

    // Util's //

    val Number.D: Double get() = this.toDouble()

    val String.rl: ResourceLocation get() = ResourceLocation.parse(this)
    val String.tryRl: ResourceLocation? get() = ResourceLocation.tryParse(this)
    val String.fullLocation: String get() {
        // Вход: "cim:example".fullLocation //

        val parts = this.split(':') // ["cim", "example"]
        if (parts.size < 2) return this
        // Если Вход="example" -> split=':' -> Выход="example" /
        // Это -> split=':' -> ["example"].size -> 1 -> 1 < 2 -> true

        val (namespace, target) = parts[0] to parts[1] // "cim" и "example"
        return "$namespace:cim_models/$target"

        // Выход: "cim:cim_models/example"
    }
    fun String.localLocation(namespace: String): String {
        // Вход: "./example.geo.json".localLocation("cim")

        if (this.startsWith("./")) {
            val location = this.removePrefix("./")
            return "$namespace:$location"

            // Выход: "namespace:example.geo.json"
        }

        return this
    }

    val String.namespace: String get() = this.split(':')[0]
    val String.location:  String get() = this.split(':')[1]

    val String.literal: Component get() = Component.literal(this)
    val String.translate: Component get() = Component.translatable(this)

    val FloatArray.toVec3: Vec3 get() = Vec3(this[0].D, this[1].D, this[2].D)
}