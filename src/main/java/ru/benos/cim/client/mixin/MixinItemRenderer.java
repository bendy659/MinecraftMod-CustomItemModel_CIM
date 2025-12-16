package ru.benos.cim.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benos.cim.client.geo.CIMItemRenderer;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void cim$render(
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay,
            BakedModel model,
            CallbackInfo ci
    ) {
        boolean isCanceled = CIMItemRenderer.INSTANCE.render(
                new CIMItemRenderer.Context(
                        itemStack,
                        displayContext,
                        leftHand,
                        poseStack,
                        bufferSource,
                        combinedLight
                )
        );

        if (isCanceled) ci.cancel();
    }
}
