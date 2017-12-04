package io.codestream.resourcemodel

interface ResourceRegistry {

    operator fun get(id: String): Resource?

    fun find(vararg attributes: Pair<String, Any?>): Collection<Resource>

}