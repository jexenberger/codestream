package io.codestream.resourcemodel

import io.codestream.util.Entry

data class Resource(val id: String, val type: String, val defn: ResourceDefinition, val attributes: Map<String, Any?>) : AbstractMap<String, Any?>() {

    override val entries: Set<Map.Entry<String, Any?>>
        get() {
            val mutableMap = mutableMapOf<String, Any?>(
                    "id" to id,
                    "type" to type,
                    "defn" to defn
            )
            mutableMap.putAll(attributes)
            return mutableMap
                    .map { (k, v) -> Entry(k, v) }
                    .toSet()
        }
}