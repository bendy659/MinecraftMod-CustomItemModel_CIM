object Parchment {
    private val data = mapOf(
        "1.21.10" to "2025.10.12",
        "1.21.9"  to "2025.10.05",
        "1.21.8"  to "2025.09.14",
        "1.21.7"  to "2025.07.18",
        "1.21.6"  to "2025.06.29",
        "1.21.5"  to "2025.06.15",
        "1.21.4"  to "2025.03.23",
        "1.21.3"  to "2024.12.07",
        "1.21.1"  to "2024.11.17",
        "1.20.6"  to "2024.06.16",
        "1.20.4"  to "2024.04.14",
        "1.20.3"  to "2023.12.31",
        "1.20.2"  to "2023.12.10",
        "1.20.1"  to "2023.09.03"
    )

    fun get(mc: String): String {
        println("Searching ParchmentMC for '$mc'...")

        // 1. Пытаемся найти идеальное совпадение
        if (data.containsKey(mc)) {
            val res = "$mc:${data[mc]}"
            println("Parchment exact match -> $res")
            return res
        }

        // 2. Если точного нет, ищем в списке
        val entry = data.entries
            .filter {
                mc.startsWith(it.key) || // Мы ищем префикс (например, для 1.21.1-fabric найдет 1.21.1)
                        it.key.startsWith(mc)    // Мы ищем "вглубь" (например, для 1.20 найдет 1.20.1)
            }
            .sortedWith(
                // ПЕРВЫЙ ПРИОРИТЕТ: Версии, которые КОРОЧЕ (например, 1.21 лучше чем 1.21.10)
                compareBy<Map.Entry<String, String>> { it.key.length }
                    // ВТОРОЙ ПРИОРИТЕТ: Самая маленькая цифра (1.20.1 вместо 1.20.6)
                    .thenBy { it.key }
            )
            .firstOrNull()

        return if (entry != null) {
            val result = "${entry.key}:${entry.value}"
            println("Parchment found closest -> $result")
            result
        } else {
            println("Parchment NOT found, using fallback.")
            "$mc:$mc"
        }
    }
}