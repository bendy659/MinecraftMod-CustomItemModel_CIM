package ru.benos.cim.client.loader

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import ru.benos.cim.client.CIM
import ru.benos.cim.client.IModLoader
import java.util.Optional

class CIMFabric: ClientModInitializer {
    private object CIMFabricLoader: IModLoader {
        private val fabricLoader = FabricLoader.getInstance()

        override val loader: String = "fabric"
        override val isLoaded: Boolean = fabricLoader.isModLoaded(CIM.MODID)
        override val modContainer: Optional<ModContainer> = fabricLoader.getModContainer(CIM.MODID)
    }

    override fun onInitializeClient() = CIM.launch(CIMFabricLoader)
}