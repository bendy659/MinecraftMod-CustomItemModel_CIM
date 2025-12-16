package ru.benos.cim.client.exception

sealed class CIMExceptionType(val k: String) {
    data object Unknown: CIMExceptionType("unknown")

    data class Custom(val message: String): CIMExceptionType(message)

    sealed class Location(k: String): CIMExceptionType("location.$k") {
        data object Unknown: Location("unknown")

        data object DirExistButFileNot: Location("dir_exist_but_file_not")
        data object FileExistButFirNot: Location("file_exist_but_file_not")
    }

    sealed class File(k: String): CIMExceptionType("file.$k") {
        data object Unknown: File("unknown")
        data object Parse: File("parse")
        data object NotExist: File("not_exist")

        sealed class CimModelsJson(k: String): File("cim_models_json.$k") {
            sealed class Entries(k: String): CimModelsJson("entries.$k") {
                data object IsNotExist: Entries("is_not_exist")
                data object IsNotArray: Entries("is_not_array")
                data object IsEmpty: Entries("is_empty")
            }

            sealed class Components(k: String): File("components.$k") {
                data object IsNotObject: Components("is_not_object")
                data object UnknownComponent: Components("unknown_component")
            }
        }

        sealed class PropertiesJson(k: String): File("properties_json.$k") {
            sealed class Require(k: String): PropertiesJson("require.$k") {
                data object Model: Require("model")
                data object Texture: Require("texture")
            }
        }
    }
}