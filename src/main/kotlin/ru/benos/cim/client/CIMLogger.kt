package ru.benos.cim.client

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CIMLogger(val base: Logger) {
    constructor(name: String) : this(LogManager.getLogger(name))

    private var pendingEntry: LogEntry? = null

    // Настройка 256-цветной палитры + Жирный шрифт (1;)
    // Синтаксис: \u001B[<STYLE>;48;5;<BG_ID>;38;5;<TEXT_ID>m
    enum class LogLevel(val ansiCode: String, val label: String) {
        INFO("\u001B[48;5;23;38;5;87m", "[INFO]"),
        DEBUG("\u001B[48;5;234;38;5;250m", "[DEBUG]"),
        WARN("\u001B[1;48;5;94;38;5;226m", "[WARN]"),
        ERROR("\u001B[1;48;5;52;38;5;196m", "[ERROR]"),
        SUCCESS("\u001B[1;48;5;22;38;5;46m", "[SUCCESS]"),

        RESET("\u001B[0m", "")
    }

    data class LogEntry(
        val message: String,
        val logLevel: LogLevel,
        var count: Int = 1
    )

    fun info(message: String)    = logging(message, LogLevel.INFO)
    fun debug(message: String)   = logging(message, LogLevel.DEBUG)
    fun warn(message: String)    = logging(message, LogLevel.WARN)
    fun error(message: String)   = logging(message, LogLevel.ERROR)
    fun success(message: String) = logging(message, LogLevel.SUCCESS) // Новый метод
    fun fatal(message: String)   = logging(message, LogLevel.ERROR)

    @Synchronized
    fun logging(message: String, logLevel: LogLevel) {
        val last = pendingEntry
        if (last != null && last.message == message && last.logLevel == logLevel) {
            last.count++
        } else {
            flush()
            pendingEntry = LogEntry(message, logLevel)
        }
    }

    @Synchronized
    fun flush() {
        val entry = pendingEntry ?: return

        val countSuffix = if (entry.count > 1) " | ${entry.count}" else ""
        val formattedMessage = "${entry.logLevel.ansiCode} ${entry.logLevel.label} ${entry.message}$countSuffix ${LogLevel.RESET.ansiCode}"

        base.info(formattedMessage)

        pendingEntry = null
    }
}