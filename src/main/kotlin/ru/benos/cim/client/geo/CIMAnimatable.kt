package ru.benos.cim.client.geo

import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import ru.benos.cim.client.CIM
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animation.AnimatableManager
import software.bernie.geckolib.animation.AnimationController
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.util.RenderUtil

class CIMAnimatable(var itemStack: ItemStack): GeoAnimatable {
    var itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar?) {
        controllers?.let {
            it.add(
                AnimationController(this) {
                    it.setAndContinue(
                        RawAnimation.begin()
                            .thenLoop("all")
                    )
                }
            )
        }
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache =
        CIMItemRenderer.getAnimatableInstanceCache(itemStack, this)

    override fun getTick(`object`: Any?): Double =
        CIM.MC.level?.gameTime?.toDouble() ?: 0.0
}