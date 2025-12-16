package ru.benos.cim.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benos.cim.client.CIMModelsRegistry;
import ru.benos.cim.client.geo.CIMItemRenderer;
import ru.benos.cim.client.serializable.CIMPropertiesData;

import java.util.Objects;

@Mixin(ItemEntityRenderer.class)
public abstract class MixinItemEntityRenderer extends EntityRenderer<ItemEntity> {
    @Shadow @Final private RandomSource random;

    @Shadow @Final private ItemRenderer itemRenderer;

    protected MixinItemEntityRenderer(EntityRendererProvider.Context context) { super(context); }

    @Inject(
            method = "render*",
            at = @At("HEAD"),
            cancellable = true
    )
    public void cim$render(
            ItemEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        poseStack.pushPose();

        ItemStack itemStack = entity.getItem();
        ResourceLocation itemId = CIMItemRenderer.INSTANCE.getItemId(itemStack);
        String modelId = CIMModelsRegistry.INSTANCE.getModelId(itemId).getPath();
        CIMPropertiesData propertiesData = Objects.requireNonNull(CIMModelsRegistry.INSTANCE.getModelData(itemId)).getProperties();

        boolean disableBobbing = propertiesData.getDisableGroundBobbing();
        boolean disableSpinning = propertiesData.getDisableGroundSpinning();

        random.setSeed(disableBobbing || disableSpinning ? 0 : ItemEntityRenderer.getSeedForItemStack(itemStack));

        BakedModel bakedModel = itemRenderer.getModel(itemStack, entity.level(), null, entity.getId());
        boolean isBlockItem = bakedModel.isGui3d();

        float f = .25f;
        float g = disableBobbing ? 0f : Mth.sin((entity.getAge() + partialTicks) / 10f + entity.bobOffs) * .1f + .1f;
        float h = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0f, g + .25f * h, 0f);

        float i = disableSpinning ? 0f : entity.getSpin(partialTicks);
        poseStack.mulPose(Axis.YP.rotation(i));

        ItemEntityRenderer.renderMultipleFromCount(itemRenderer, poseStack, buffer, packedLight, itemStack, bakedModel, isBlockItem, random);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        ci.cancel();
    }
}