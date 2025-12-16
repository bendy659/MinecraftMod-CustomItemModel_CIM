package ru.benos.cim.client.gui

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.network.chat.Component
import ru.benos.cim.client.CIM.literal
import ru.benos.cim.client.CIM.translate

class CIMScreenOwO: BaseOwoScreen<StackLayout>() {
    companion object { val NEW: CIMScreenOwO = CIMScreenOwO() }

    private var WARNINGS: MutableList<String> = mutableListOf()
    private var ERRORS:   MutableList<String> = mutableListOf()

    private val HEADER: Component = "cim.screen.header".translate
    private val LABELS: MutableList<Component> = mutableListOf()
    private val LABELS_COUNT: Int = 4

    private val BACKGROUND_COLOR = Color(0f, 0f, 0f, 0.75f)
    private val HEADER_COLOR = Color(1f, 0.5f, 0f)
    private val LABELS_COLOR = Color(0.75f, 0.75f, 0.75f)

    override fun createAdapter(): OwoUIAdapter<StackLayout> =
        OwoUIAdapter.create(this, Containers::stack)

    override fun build(rootComponent: StackLayout) {
        List(LABELS_COUNT) { it }.forEach { LABELS += "cim.screen.label.$it".translate }

        rootComponent
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
            .surface(Surface.panorama(PANORAMA, true))

        val background = Components.box(Sizing.fill(), Sizing.fill())
        background
            .color(BACKGROUND_COLOR)
            .fill(true)
        rootComponent.child(background)

        val headerComponent = Components.label(HEADER)
        headerComponent
            .color(HEADER_COLOR)
            .shadow(true)
            .horizontalTextAlignment(HorizontalAlignment.CENTER)
            .verticalTextAlignment(VerticalAlignment.TOP)
            .sizing(Sizing.fill())
            .margins(Insets.top(64))
            .zIndex(1)
        rootComponent.child(headerComponent)

        val scrollContainer = Containers.verticalFlow(Sizing.fill(), Sizing.fill())
        scrollContainer
            .margins(Insets.top(32))
            .padding(Insets.of(16))
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)

        val labels = LABELS.joinToString("") { it.string + "\n" }.literal
        val labelsComponent = Components.label(labels)
        labelsComponent
            .color(LABELS_COLOR)
            .shadow(true)
            .horizontalTextAlignment(HorizontalAlignment.CENTER)
            .verticalTextAlignment(VerticalAlignment.CENTER)
            .sizing(Sizing.fill())
            .zIndex(1)
        scrollContainer.child(labelsComponent)

        val vScroll = Containers.verticalScroll(Sizing.fill(), Sizing.fill(), scrollContainer)
        rootComponent.child(vScroll)
    }
}