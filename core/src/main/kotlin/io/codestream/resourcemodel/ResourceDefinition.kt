package io.codestream.resourcemodel

import io.codestream.core.Parameter
import io.codestream.util.Entry

data class ResourceDefinition(var type: String,
                              var desc: String,
                              var attributes: Array<Parameter>) : AbstractMap<String, Parameter>() {
    override val entries: Set<Map.Entry<String, Parameter>>
        get() = attributes.map { Entry(it.name, it) }.toSet()


}