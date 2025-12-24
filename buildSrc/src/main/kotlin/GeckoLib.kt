object GeckoLib {
    private val fabric = mapOf(
        "1.21.11" to "5.4",
        "1.21.10" to "5.3",
        "1.21.9" to null,
        "1.21.8" to "5.2.1",
        "1.21.7" to "5.2.1",
        "1.21.6" to "5.1.0",
        "1.21.5" to "5.0",
        "1.21.4" to "4.8",
        "1.21.3" to "4.7",
        "1.21.2" to null,
        "1.21.1" to "4.5.8",
        "1.21" to "4.5.5",

        "1.20.6" to "4.5.1",
        "1.20.5" to "4.5",
        "1.20.4" to "4.3.1",
        "1.20.3" to "4.3.1",
        "1.20.2" to "4.2.4",
        "1.20.1" to "4.2.1",
        "1.20" to "4.2"
    )
    private val forge = mapOf(
        "1.21.11" to "5.4",
        "1.21.10" to "5.3",
        "1.21.9" to null,
        "1.21.8" to "5.2.1",
        "1.21.7" to "5.2.0",
        "1.21.6" to "5.1.0",
        "1.21.5" to "5.0",
        "1.21.4" to "4.8",
        "1.21.3" to "4.7",
        "1.21.2" to null,
        "1.21.1" to "4.5.8",
        "1.21" to "4.5",

        "1.20.6" to "4.5.4",
        "1.20.5" to null,
        "1.20.4" to "4.3.1",
        "1.20.3" to "4.3.1",
        "1.20.2" to "4.3.1",
        "1.20.1" to "4.2.2",
        "1.20" to "4.2.2"
    )
    private val neoforge = mapOf(
        "1.21.11" to "5.4",
        "1.21.10" to "5.3",
        "1.21.9" to null,
        "1.21.8" to "5.2.1",
        "1.21.7" to "5.2.0",
        "1.21.6" to "5.1.0",
        "1.21.5" to "5.0",
        "1.21.4" to "4.8",
        "1.21.3" to "4.7",
        "1.21.2" to null,
        "1.21.1" to "4.5.8",
        "1.21" to "4.5",

        "1.20.6" to "4.5.4",
        "1.20.5" to "4.5",
        "1.20.4" to "4.3.1",
        "1.20.3" to "4.3.1",
        "1.20.2" to "4.3.1",
        "1.20.1" to "4.2.2",
        "1.20" to "4.2.2"
    )

    fun get(loader: String, minecraft: String): String {
        val result = when (loader) {
            "fabric"   -> fabric[minecraft]
            "forge"    -> forge[minecraft]
            "neoforge" -> neoforge[minecraft]
            else -> null
        }

        result ?: throw Exception("Could not exist 'GeckoLib' version for '$minecraft/$loader'!")

        return result
    }
}