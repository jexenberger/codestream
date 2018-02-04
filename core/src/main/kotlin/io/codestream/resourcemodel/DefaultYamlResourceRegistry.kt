package io.codestream.resourcemodel

import io.codestream.runtime.CodestreamRuntime
import io.codestream.util.YamlFactory
import io.codestream.util.log.Log
import java.io.FileReader

@Suppress("UNCHECKED_CAST")
class DefaultYamlResourceRegistry(val filePath: String, val log: Log = CodestreamRuntime.log) : ResourceRegistry {

    protected var registry: Map<String, Resource> = emptyMap()

    fun load(): ResourceError? {
        val raw = YamlFactory.yaml().load(FileReader(filePath)) as Map<String, Any?>

        return try {
            val data = raw["resources"]?.let { it as List<Map<String, Any?>> } ?: throw ResourceError(listOf("resources is a required attribute"))
            loadAbstracts(data)
            registry = data
                    .filter { !"abstract".equals(it["type"]!!.toString()) }
                    .map { createResourceDefinition(it) }
                    .map { it.id to it }
                    .toMap()
            null
        } catch (e: ResourceError) {
            return e
        }
    }

    private fun createResourceDefinition(it: Map<String, Any?>): Resource {
        val id = it["id"]!!.toString()
        val type = it["type"]!!.toString()
        val inherits = it["inherits"] as String?
        val attributes = it["attributes"]?.let { (it as Map<String, Any>) } ?: throw ResourceError(listOf("No attributes defined for $id::$type"))
        return ResourceDefinitions[type]?.let { defn ->
            val attrMap = loadAbstractType(inherits)
            attrMap.putAll(attributes)
            val boundMap = attrMap.mapValues { value ->
                val parameter = defn.attributes.find { it.name.equals(value.key) }
                parameter?.let { type ->
                    val result = type.tryConvert(value.value)
                    result.mapR { throw ResourceError(it) }
                            .left
                } ?: throw ResourceError(listOf("${value.key} is not defined in ResourceDefinition: '${defn.type}'"))
            }
            Resource(
                    id = id,
                    type = type,
                    defn = defn,
                    attributes = boundMap
            )
        } ?: throw  ResourceError(listOf("$type is not defined as a resource"))
    }

    private fun loadAbstractType(inherits: String?): MutableMap<String, Any?> {
        //we do it this way due to some wierd interoperability issues with Java/SnakeYaml
        //it actually comes as a true null, not an optional
        if (inherits == null) {
            return mutableMapOf()
        }
        return ResourceDefinitions.getAbstract(inherits)?.let {
            val map = mutableMapOf<String, Any?>()
            map.putAll(it)
            map
        } ?: mutableMapOf()
    }

    private fun loadAbstracts(data: List<Map<String, Any?>>) {
        //get all the abstracts
        data.filter {
            val id = it["id"]?.let { it as String } ?: throw ResourceError(listOf("id is not defined"))
            val type = it["type"]?.let { it as String } ?: throw ResourceError(listOf("type is not defined for $id"))
            "abstract".equals(type)
        }.forEach {
            val id = it["id"]!!.toString()
            val attributes = it["attributes"]?.let { (it as Map<String, Any>) } ?: emptyMap<String, Any?>()
            log.debug("Added abstract type $id")
            ResourceDefinitions.addAbstract(id, attributes)
        }
    }

    override operator fun get(id: String): Resource? {
        return registry[id]
    }

    override fun findByType(type: String): Collection<Resource> {
        return registry.values.filter { type.toString() == it.type }.toList()
    }

    override fun find(attributes: Map<String, Any?>): Collection<Resource> {
        if (attributes.isEmpty()) {
            return registry.values
        }
        return this.registry.values.filter { resource ->
            attributes.map {
                resource[it.key]?.equals(it.value) ?: false
            }.foldRight(true) { a, b -> a and b }
        }
    }
}