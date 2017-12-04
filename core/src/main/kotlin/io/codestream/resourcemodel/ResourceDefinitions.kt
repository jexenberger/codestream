package io.codestream.resourcemodel

object ResourceDefinitions {

    val abstractMap: MutableMap<String, Map<String, Any?>> = mutableMapOf()

    val defnMap: MutableMap<String, ResourceDefinition> by lazy {
        val resources = mutableMapOf<String, ResourceDefinition>()
        resources
    }

    operator fun set(id: String, defn: ResourceDefinition) {
        defnMap[id] = defn
    }

    operator fun get(id: String): ResourceDefinition? {
        return defnMap[id]
    }

    fun addAbstract(name: String, attributes: Map<String, Any?>) {
        abstractMap[name] = attributes
    }

    fun getAbstract(name: String): Map<String, Any?>? {
        return abstractMap[name]
    }


}