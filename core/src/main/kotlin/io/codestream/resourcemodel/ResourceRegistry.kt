package io.codestream.resourcemodel

interface ResourceRegistry {

    operator fun get(id: String): Resource?

    fun findByAttributes(vararg attributes: Pair<String, Any?>) = find(attributes.toMap())

    fun find(attributes: Map<String, Any?>): Collection<Resource>

    fun findByType(type: String): Collection<Resource>

}