object OwoLib {
    private val fabric = mapOf(
        "1.21.11" to null,
        "1.21.10" to "0.12.22",
        "1.21.9" to "0.12.22",
        "1.21.8" to "0.12.22",
        "1.21.7" to "0.12.22",
        "1.21.6" to "0.12.22",
        "1.21.5" to "0.12.21",
        "1.21.4" to "0.12.20",
        "1.21.3" to "0.12.18",
        "1.21.2" to "0.12.8",
        "1.21.1" to "0.12.11",
        "1.21" to "0.12.11",

        "1.20.6" to "0.12.9",
        "1.20.5" to "0.12.9",
        "1.20.4" to "0.12.3",
        "1.20.3" to "0.12.3",
        "1.20.2" to "0.11.4",
        "1.20.1" to "0.11.1",
        "1.20" to "0.11.1"
    )
    private val neoforge = mapOf(
        "1.21.11" to null,
        "1.21.10" to "0.12.28",
        "1.21.9" to "0.12.25",
        "1.21.8" to null,
        "1.21.7" to null,
        "1.21.6" to null,
        "1.21.5" to null,
        "1.21.4" to null,
        "1.21.3" to null,
        "1.21.2" to null,
        "1.21.1" to null,
        "1.21" to null,

        "1.20.6" to null,
        "1.20.5" to null,
        "1.20.4" to null,
        "1.20.3" to null,
        "1.20.2" to null,
        "1.20.1" to null,
        "1.20" to null
    )

    fun get(loader: String, minecraft: String): String {
        val result = when (loader) {
            "fabric"   -> fabric[minecraft]
            //"forge"    -> forge[minecraft]
            "neoforge" -> neoforge[minecraft]
            else -> null
        }

        result ?: throw Exception("Could not exist 'owo-lib' version for '$minecraft/$loader'!")

        return result
    }
}