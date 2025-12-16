package ru.benos.cim.client.patch

//? if fabric {
//? }
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import ru.benos.cim.client.CIM.translate
import ru.benos.cim.client.gui.CIMScreenOwO

object MainMenuPatch {
    val bLabel: MutableComponent  = "cim.screen.header".translate
        .copy().withStyle(ChatFormatting.GOLD)

    val apply: Unit
        get() {

            //? if fabric {
            ScreenEvents.AFTER_INIT.register { client, screen, width, height ->
                if (screen is TitleScreen) {
                    val buttons = Screens.getButtons(screen)

                    // 1. Ищем якорь - кнопку "Настройки" (Options)
                    // Она отмечает начало нижней секции, которую мы будем двигать.
                    val optionsButton = buttons.find { widget ->
                        val contents = widget.message.contents
                        contents is TranslatableContents && contents.key == "menu.options"
                    } as? AbstractWidget

                    if (optionsButton != null) {
                        val originalOptionsY = optionsButton.y
                        val buttonHeight = 20
                        val padding = 4
                        val shiftAmount = buttonHeight + padding // 24px

                        // 2. Ищем "дно" верхней группы кнопок.
                        // Нам нужна кнопка, которая находится ниже всех, НО строго выше "Настроек".
                        var maxUpperY = 0
                        var foundUpperGroup = false

                        buttons.forEach { widget ->
                            if (widget is AbstractWidget && widget.visible) {
                                // Если кнопка выше линии настроек (с запасом 5px на погрешность)
                                if (widget.y < originalOptionsY - 5) {
                                    val widgetBottom = widget.y + widget.height
                                    if (widgetBottom > maxUpperY) {
                                        maxUpperY = widgetBottom
                                        foundUpperGroup = true
                                    }
                                }
                            }
                        }

                        // 3. Вычисляем позицию для нашей кнопки
                        // Если нашли верхнюю группу -> ставим через 4px после неё.
                        // Если вдруг не нашли (странный мод) -> ставим прямо над настройками.
                        val newButtonY = if (foundUpperGroup) {
                            maxUpperY + padding
                        } else {
                            originalOptionsY
                        }

                        // 4. Сдвигаем НИЖНЮЮ группу (Настройки, Выход и иконки) вниз.
                        // Мы сдвигаем всё, что было на уровне оригинальных настроек или ниже.
                        buttons.forEach { widget ->
                            if (widget is AbstractWidget) {
                                if (widget.y >= originalOptionsY) {
                                    widget.y += shiftAmount
                                }
                            }
                        }

                        // 5. Создаем и добавляем кнопку
                        val cimBtnWidth = 200
                        val centerX = width / 2

                        val cimButton = Button.builder(bLabel) { client.setScreen(CIMScreenOwO.NEW) }
                            .bounds(
                                centerX - (cimBtnWidth / 2),
                                newButtonY, // Позиция сразу под верхними кнопками
                                cimBtnWidth,
                                buttonHeight
                            )
                            .build()

                        buttons.add(cimButton)
                    }
                }
            }
            //? }
            //? if neoforge || forge {
            Unit
            //? }
        }
}