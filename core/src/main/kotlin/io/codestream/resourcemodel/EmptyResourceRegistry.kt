package io.codestream.resourcemodel

class EmptyResourceRegistry : ResourceRegistry {
    override fun get(id: String): Resource? {
        return null
    }


    override fun find(vararg attributes: Pair<String, Any?>): Collection<Resource> {
        return emptyList()
    }
}