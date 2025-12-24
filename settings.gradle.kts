pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins { id("dev.kikugie.stonecutter") version "0.7.1" }

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun expandVersion(str: String): List<String> {
            // Input: 1.20x6

            val parts = str.split("x") // 1.20, 6
            if (parts.size < 2) return listOf(str)

            val base = parts[0] // 1.20
            val patches = parts[1].toIntOrNull() ?: 0 // 6

            val versions = mutableListOf(base) // + 1.20

            for (i in 1..patches) versions += "$base.$i"

            return versions // 1.20, 1.20.1, 1.20.2, ..., 1.20.6
        }

        fun mc(loader: String, versionsStr: String) =
            expandVersion(versionsStr).forEach { version -> vers("$version-$loader", version) }

        fun fabric(versions: String) = mc("fabric", versions)
        fun forge(versions: String) = mc("forge", versions)
        fun neoforge(versions: String) = mc("neoforge", versions)

        fun full(versions: String) { fabric(versions); forge(versions); neoforge(versions) }

        mc("fabric","1.21.1")

        full("1.20x6")
        full("1.21x11")
    }
    create(rootProject)
}

rootProject.name = "CustomItemModel (CIM)"