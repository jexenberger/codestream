package io.codestream.resourcemodel

class EmptyResourceRegistry : ResourceRegistry {

    override fun find(attributes: Map<String, Any?>): Collection<Resource> {
        return emptyList()
    }

    override fun findByType(type: String): Collection<Resource> {
        return emptyList()
    }

    override fun get(id: String): Resource? {
        return null
    }


}