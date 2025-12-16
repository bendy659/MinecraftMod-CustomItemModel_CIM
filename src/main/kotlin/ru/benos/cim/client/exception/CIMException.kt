package ru.benos.cim.client.exception

import ru.benos.cim.client.CIM
import ru.benos.cim.client.CIM.translate
import ru.benos.cim.client.CIMLogger

open class CIMException(
    private val type: CIMExceptionType,
    private val logLevel: CIMLogger.LogLevel = CIMLogger.LogLevel.WARN,
    private val exception: Exception? = null,
    private val push: (String) -> Boolean = { true },
    private val args: Array<out Any> = emptyArray()
) : RuntimeException() {

    companion object {
        // 1. Постоянный список игнора (Blacklist)
        private val PERMANENT_MUTES = java.util.Collections.synchronizedSet(HashSet<CIMExceptionType>())

        // 2. Одноразовый список (One-Shot)
        // Используем ConcurrentHashMap.newKeySet() для потокобезопасности
        private val ONE_SHOT_MUTES = java.util.concurrent.ConcurrentHashMap.newKeySet<CIMExceptionType>()

        // --- API ---

        // Заглушить навсегда
        fun muteForever(type: CIMExceptionType) = PERMANENT_MUTES.add(type)
        fun unmuteForever(type: CIMExceptionType) = PERMANENT_MUTES.remove(type)

        // Заглушить ТОЛЬКО СЛЕДУЮЩИЙ
        fun muteNext(type: CIMExceptionType) {
            ONE_SHOT_MUTES.add(type)
        }

        // Проверка: нужно ли логировать?
        private fun shouldLog(type: CIMExceptionType): Boolean {
            // А. Если заглушен навсегда — не логируем
            if (PERMANENT_MUTES.contains(type)) return false

            // Б. Если заглушен одноразово — снимаем флаг и не логируем
            // .remove(obj) вернет true, только если объект был в сете.
            // Флаг сгорает именно в этот момент.
            if (ONE_SHOT_MUTES.remove(type)) return false

            // В. Иначе логируем
            return true
        }
    }

    override val message: String get() = resolveMessage()

    init {
        // Проверяем через общий метод
        if (shouldLog(type)) {
            processLog()
        }
    }

    private fun processLog() {
        if (exception != null) {
            val str = exception.stackTraceToString()
            if (push(str)) CIM.LOGGER.logging(str, logLevel)
        }
        CIM.LOGGER.logging(message, logLevel)
    }

    private fun resolveMessage(): String {
        return "cim.exception.${type.k}"
            .translate
            .string
            .params(args)
    }

    private fun String.params(args: Array<out Any>): String =
        Regex("#p(\\d+)").replace(this) { match ->
            val idx = match.groupValues[1].toInt()
            (args.getOrNull(idx) ?: match.value).toString()
        }
}