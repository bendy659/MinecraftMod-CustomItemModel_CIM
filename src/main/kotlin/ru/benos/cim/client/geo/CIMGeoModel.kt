package ru.benos.cim.client.geo

import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import ru.benos.cim.client.CIM.rl
import ru.benos.cim.client.CIMModelsRegistry
import software.bernie.geckolib.animation.Animation
import software.bernie.geckolib.animation.AnimationProcessor
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.model.GeoModel

class CIMGeoModel(
    val itemId: ResourceLocation,
    var itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE
): GeoModel<CIMAnimatable>() {
    private var currentModel: BakedGeoModel? = null
    private val processor: AnimationProcessor<CIMAnimatable> = AnimationProcessor(this)

    @Deprecated("Deprecated in Java")
    override fun getModelResource(animatable: CIMAnimatable): ResourceLocation =
        CIMModelsRegistry.getModelLocation(itemId, animatable.itemDisplayContext).rl

    @Deprecated("Deprecated in Java")
    override fun getTextureResource(animatable: CIMAnimatable): ResourceLocation =
        CIMModelsRegistry.getTextureLocation(itemId, animatable.itemDisplayContext).rl

    override fun getAnimationResource(animatable: CIMAnimatable): ResourceLocation =
        CIMModelsRegistry.getAnimationsLocation(itemId, animatable.itemDisplayContext).rl

    override fun getBakedModel(location: ResourceLocation): BakedGeoModel? {
        val modelId = CIMModelsRegistry.getModelId(itemId)
        val bakedModel = CIMModelsRegistry.getBakedModel(modelId, itemDisplayContext)

        if (currentModel != bakedModel) {
            processor.setActiveModel(bakedModel)
            currentModel = bakedModel
        }

        return currentModel
    }

    override fun getAnimation(animatable: CIMAnimatable, name: String?): Animation? {
        name ?: return null

        val modelId = CIMModelsRegistry.getModelId(itemId)
        val bakedAnimations = CIMModelsRegistry.getBakedAnimations(modelId, itemDisplayContext)
        val animation = bakedAnimations?.getAnimation(name)

        return animation
    }

    override fun getAnimationProcessor(): AnimationProcessor<CIMAnimatable> = processor

    override fun getRenderType(animatable: CIMAnimatable?, texture: ResourceLocation): RenderType =
        RenderType.entityTranslucent(texture)
}