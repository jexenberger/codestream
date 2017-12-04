package io.codestream.resourcemodel

data class Resource(val id: String, val defn: ResourceDefinition, val attributes: Map<String, Any?>) {

    operator fun get(attrName: String): Any? {
        return attributes[attrName]
    }


}