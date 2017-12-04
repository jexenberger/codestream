package io.codestream.resourcemodel

import io.codestream.util.Either
import io.codestream.util.YamlFactory
import java.io.FileReader

class DefaultYamlResourceRegistry(val filePath: String) : ResourceRegistry {

    protected var registry: Map<String, Resource> = emptyMap()

    fun load(): ResourceError? {
        val raw = YamlFactory.yaml().load(FileReader(filePath)) as Map<String, Any?>
        return Either.onException<Map<String, Resource>, ResourceError> {
            val data = raw["resources"]?.let { it as List<Map<String, Any?>> } ?: throw ResourceError(listOf("resources is a required attribute"))
            loadAbstracts(data)
            val resources = data.filter {
                val type = it["type"]!!.toString()
                !"abstract".equals(type)
            }.map {
                val id = it["id"]!!.toString()
                val type = it["type"]!!.toString()
                val inherits = it["inherits"] as String
                val attributes = it["attributes"]?.let { (it as List<Map<String, Any>>).map { it["name"].toString() to it["value"] }.toMap() } ?: emptyMap<String, Any?>()
                ResourceDefinitions[type]?.let { defn ->
                    val attrMap = inherits?.let { inherited ->
                        ResourceDefinitions.getAbstract(inherited)?.let {
                            val map = mutableMapOf<String, Any?>()
                            map.putAll(it)
                            map
                        } ?: throw ResourceError(listOf("$inherited is not defined as an abstract"))
                    }
                    attrMap.putAll(attributes)
                    val boundMap = attrMap.map { value ->
                        defn.attributes.find {
                            it.name.equals(value.key)
                        }?.let { type ->
                            val result = type.tryConvert(value.value).map({
                                Pair(value.key, it)
                            }, {
                                throw ResourceError(it)
                            })
                            result
                        } ?: throw ResourceError(listOf("${value.key} is not defined in ResourceDefinition: '${defn.type}'"))
                    }.toMap()
                    Resource(
                            id = id,
                            defn = defn,
                            attributes = boundMap
                    )
                } ?: throw  ResourceError(listOf("$type is not defined as a resource"))
            }.map {
                Pair(it.id, it)
            }.toMap()
            resources
        }.map({
            this.registry = it
            null
        }, {
            it
        })
    }

    private fun loadAbstracts(data: List<Map<String, Any?>>) {
        //get all the abstracts
        data.filter {
            val id = it["id"]?.let { it as String } ?: throw ResourceError(listOf("id is not defined"))
            val type = it["type"]?.let { it as String } ?: throw ResourceError(listOf("type is not defined for $id"))
            "abstract".equals(type)
        }.forEach {
            val id = it["id"]!!.toString()
            val attributes = it["attributes"]?.let { (it as List<Map<String, Any>>).map { it["name"].toString() to it["value"] }.toMap() } ?: emptyMap<String, Any?>()
            ResourceDefinitions.addAbstract(id, attributes);
        }
    }

    override operator fun get(id: String): Resource? {
        return registry[id]
    }

    override fun find(vararg attributes: Pair<String, Any?>): Collection<Resource> {
        if (attributes.isEmpty()) {
            return registry.values
        }
        return this.registry.values.filter { resource ->
            var found = true
            attributes.forEach { attr ->
                if (resource.attributes.containsKey(attr.first)) {
                    found = found and (resource[attr.first]?.equals(attr.second) ?: false)
                } else {
                    found = false
                }
            }
            found
        }
    }
}