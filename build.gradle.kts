import java.util.*

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin")
    id("com.github.johnrengelman.shadow")

    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.0"
}

val minecraft = stonecutter.current.version
val loader = loom.platform.get().name.lowercase()

val owoLibVersion = minecraft
    .split('.')
    .take(2)
    .joinToString(".")


version = "${mod.version}+$minecraft"
group = mod.group

base { archivesName.set("${mod.id}-$loader") }

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.project.prop("loom.platform")
})
repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.nucleoid.xyz/")

    maven("https://maven.parchmentmc.org")

    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroup("com.eliotlash.mclib")
        }
    }

    maven("https://maven.wispforest.io")
}
dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${Parchment.get(minecraft)}@zip")
    })

    // Architectury API //
    modImplementation("dev.architectury:architectury-$loader:${mod.dep("architectury_api")}")

    // Kotlin lib's //
    implementation( kotlin("stdlib") )
    implementation( kotlin("reflect") )
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Kotlin Fabric Language //
    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.6+kotlin.2.2.20")

    // GeckoLib //
    modImplementation("software.bernie.geckolib:geckolib-$loader-$minecraft:${GeckoLib.get(loader, minecraft)}")
    implementation("com.eliotlash.mclib:mclib:20")

    // OwO-lib //
    modImplementation("io.wispforest:owo-lib:${OwoLib.get(loader, minecraft)}+$minecraft")
    include("io.wispforest:owo-sentinel:${OwoLib.get(loader, minecraft)}+$minecraft")

    if (loader == "fabric") {
        modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

        modImplementation("net.fabricmc.fabric-api:fabric-api:${FabricApi.get(minecraft)}+$minecraft")

    }
    if (loader == "forge") {
        "forge"("net.minecraftforge:forge:${minecraft}-${mod.dep("forge_loader")}")

        "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
            implementation(it)
            include(it)
        }
    }
    if (loader == "neoforge") {
        "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")
    }
}

loom {
    mixin { defaultRefmapName.set("${mod.id}-refmap.json") }

    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    decompilers { get("vineflower").apply { options.put("mark-corresponding-synthetics", "1") } }

    if (loader == "forge") forge.mixinConfigs("${mod.id}-common.mixins.json", "${mod.id}-forge.mixins.json")

    runConfigs.all {
        if (environment == "client") {
            programArg("--username=_BENDY659_")
            programArg("--uuid=dbbbde03-1813-4364-82b7-4738f555aaf7")
        }
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) localProperties.load(localPropertiesFile.inputStream())

publishMods {
    val modrinthToken = localProperties.getProperty("publish.modrinthToken", "")
    val curseforgeToken = localProperties.getProperty("publish.curseforgeToken", "")


    file = project.tasks.remapJar.get().archiveFile
    dryRun = modrinthToken == null || curseforgeToken == null

    displayName = "${mod.name} ${loader.replaceFirstChar { it.uppercase() }} ${property("mod.mc_title")}-${mod.version}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = BETA

    modLoaders.add(loader)

    val targets = property("mod.mc_targets").toString().split(' ')

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-api")
            optional("modmenu")
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-api")
            optional("modmenu")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.jar {  archiveClassifier = "dev" }

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep_fabric"),
        "author" to mod.prop("author")
    )
    properties(
        listOf("META-INF/mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep_forgelike"),
        "author" to mod.prop("author")
    )
    properties(
        listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep_forgelike"),
        "author" to mod.prop("author")
    )
    properties(
        listOf("cim-fabric.mixins.json", "cim-forge.mixins.json", "cim-neoforge.mixins.json"),
        "id" to mod.id,
        "id" to mod.id,
        "version" to minecraft,
        "loader" to loader
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.remapJar { archiveFileName.set("CIM-$minecraft-$loader-${mod.prop("version")}.jar") }

tasks.shadowJar { archiveClassifier.set("dev-shadow") }