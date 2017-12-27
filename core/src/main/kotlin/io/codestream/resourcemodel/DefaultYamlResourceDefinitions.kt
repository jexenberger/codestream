package io.codestream.resourcemodel

import io.codestream.core.Parameter
import io.codestream.runtime.CodestreamRuntime
import io.codestream.util.Either
import io.codestream.util.YamlFactory
import io.codestream.util.log.Log
import java.io.File
import java.io.FileInputStream

@Suppress("UNCHECKED_CAST")
class DefaultYamlResourceDefinitions(val definitionsFile: String, log: Log = CodestreamRuntime.runtime.log) {

    fun load(): Either<Unit, ResourceError> {
        return Either.onException {
            val file = File(definitionsFile)
            if (!file.exists() || file.isDirectory) {
                throw ResourceError(listOf("File $definitionsFile does not exist or is directory"))
            }
            val result = YamlFactory.yaml().load(FileInputStream(file)) as Map<String, Any?>
            val definitions = result["definitions"] as List<Map<String, Any?>>
            definitions.forEach { defn ->
                val name = defn["name"]?.toString() ?: throw ResourceError(listOf("name is a is required property for a definition"))
                val desc = defn["desc"]?.toString() ?: ""
                val attributes = (defn["attributes"] as List<Map<String, Any?>>?)
                val parms: Array<Parameter> = attributes?.map { attr ->
                    val attrName = attr["name"]?.toString() ?: throw ResourceError(listOf("name is a is required property of an attribute for definition $name"))
                    val type = attr["type"]?.toString() ?: throw ResourceError(listOf("type is a is required property of an attribute for definition $name"))
                    val required: Boolean = attr["required"]?.let { it as Boolean } ?: true
                    val defaultString = attr["default"]?.toString()
                    val valueList = attr["values"]?.toString()
                    Parameter(
                            name = attrName,
                            desc = desc,
                            stringType = type,
                            defaultString = defaultString,
                            required = required,
                            allowedValuesList = valueList

                    )
                }?.toTypedArray() ?: throw ResourceError(listOf("attributes is a required property for defintion $name"))
                ResourceDefinitions[name] = ResourceDefinition(name, desc, parms)
            }
        }
    }

}