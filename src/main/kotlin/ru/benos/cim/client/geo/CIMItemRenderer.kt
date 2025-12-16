package ru.benos.cim.client.geo

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.CustomModelData
import ru.benos.cim.client.CIM
import ru.benos.cim.client.CIMModelsRegistry
import ru.benos.cim.client.exception.CIMException
import ru.benos.cim.client.exception.CIMExceptionType
import ru.benos.cim.client.serializable.CIMComponentMode
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.renderer.GeoObjectRenderer
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*

object CIMItemRenderer {
    var itemIdCache: WeakHashMap<ItemStack, ResourceLocation> = WeakHashMap()
    var animatableCache: WeakHashMap<ItemStack, CIMAnimatable> = WeakHashMap()
    var geoModelCache: WeakHashMap<ResourceLocation, CIMGeoModel> = WeakHashMap()
    var rendererCache: WeakHashMap<ResourceLocation, GeoObjectRenderer<CIMAnimatable>> = WeakHashMap()

    fun clearCache() =
        listOf(itemIdCache, animatableCache, geoModelCache).forEach { it.clear() }

    data class Context(
        val itemStack: ItemStack,
        val displayContext: ItemDisplayContext,
        val isLeftHand: Boolean,
        val poseStack: PoseStack,
        val buffer: MultiBufferSource,
        val lightOverlay: Int
    )

    fun renderer(ctx: Context): Boolean {
        val itemId = getItemId(ctx.itemStack)
        if (ctx.itemStack.isEmpty) return false

        val itemFiltered = CIMModelsRegistry.getModelData(itemId)
        itemFiltered ?: return false

        val componentsMatched = ctx.itemStack.matchesComponents(itemFiltered.components, itemFiltered.mode)
        if (!componentsMatched) return false

        val animatable = getAnimatable(ctx.itemStack)

        val cimGeoModel = getGeoModel(itemId)

        try {
            ctx.poseStack.pushPose()

            // Применение трансформации //
            val displayTransform = CIMModelsRegistry.getDisplayTransform(CIMModelsRegistry.getModelId(itemId).path, ctx.displayContext)
            displayTransform.apply(ctx.isLeftHand, ctx.poseStack)

            // Центрирование
            ctx.poseStack.translate(-.5, -.5, -.5)

            // Финальный рендер //
            val renderer = getRenderer(itemId)
            renderer.render(
                ctx.poseStack,
                animatable,
                ctx.buffer,
                null,
                null,
                ctx.lightOverlay,
                RenderSystem.getShaderGameTime()
            )
        } catch (e: Exception) {
            CIMException(
                type = CIMExceptionType.Custom("XDDDS"),
                exception = e
            )
        } finally { ctx.poseStack.popPose() }

        return true
    }

    fun getItemId(itemStack: ItemStack): ResourceLocation =
        itemIdCache.computeIfAbsent(itemStack) { BuiltInRegistries.ITEM.getKey(itemStack.item) }

    fun getAnimatable(itemStack: ItemStack): CIMAnimatable =
        animatableCache.computeIfAbsent(itemStack) { CIMAnimatable(itemStack) }

    fun getGeoModel(itemId: ResourceLocation): CIMGeoModel =
        geoModelCache.computeIfAbsent(itemId) { CIMGeoModel(itemId) }

    fun getRenderer(itemId: ResourceLocation, ): GeoObjectRenderer<CIMAnimatable> =
        rendererCache.computeIfAbsent(itemId) { GeoObjectRenderer(getGeoModel(itemId)) }

    fun ItemStack.matchesComponents(requiredPatch: DataComponentPatch?, mode: CIMComponentMode): Boolean {
        // 1. Если фильтр пустой — считаем, что подходит любой предмет (или false, зависит от твоей логики)
        if (requiredPatch == null || requiredPatch.isEmpty) return true

        val patchEntries = requiredPatch.entrySet()

        // Вспомогательная функция: проверяет совпадение ОДНОГО компонента
        fun checkSingleEntry(entry: Map.Entry<*, *>): Boolean {
            // Хаки с дженериками, чтобы котлин не ругался на типы
            val type = entry.key as net.minecraft.core.component.DataComponentType<*>
            val expectedValueOpt = entry.value as java.util.Optional<*>

            // Получаем реальное значение предмета
            val itemValue = this.get(type)

            return if (expectedValueOpt.isPresent) {
                // Требование: Компонент должен быть И должен быть равен ожидаемому
                val expected = expectedValueOpt.get()
                // itemValue != null проверяет наличие, equals проверяет значение
                itemValue != null && itemValue == expected
            } else
            // Требование: Компонента быть НЕ должно (explicit removal)
                itemValue == null
        }

        return when (mode) {
            CIMComponentMode.ANY -> patchEntries.any { checkSingleEntry(it) }
            CIMComponentMode.ALL -> patchEntries.all { checkSingleEntry(it) }
            CIMComponentMode.ONLY -> {
                // А. Сначала проверяем, что все требуемые на месте (как в ALL)
                val allRequiredMatched = patchEntries.all { checkSingleEntry(it) }
                if (allRequiredMatched) return true

                // Б. Проверяем, нет ли у предмета "левых" компонентов
                // Получаем список всех компонентов, которые реально висят на предмете
                val itemComponents = this.components

                // Пробегаемся по всем компонентам предмета
                // Если найдем хоть один, которого нет в keys патча — FAIL
                // Внимание: patchEntries содержит Map.Entry, ключи - это DataComponentType
                val requiredTypes = patchEntries.map { it.key }.toSet()

                val hasExtra = itemComponents.keySet().any { typeOnItem ->
                    !requiredTypes.contains(typeOnItem)
                }

                return !hasExtra
            }
        }
    }
}