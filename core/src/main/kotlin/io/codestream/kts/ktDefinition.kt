package io.codestream.kts

import io.codestream.core.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

open class ktDefinition(val id: TaskId, val type: TaskType, private val lineNumber: Long = 0) {

    var condition: Conditional = defaultCondition()


    val defn: ExecutableDefinition<Executable>
        get() {
            return ExecutableDefinition(
                    type = type, id = id,
                    binding = MapBinding(id, type, attributeMap()).toBinding(),
                    condition = condition,
                    lineNumber = lineNumber
            )
        }

    internal fun attributeMap(): Map<String, Any?> {
        return this::class.memberProperties
                .filter { it.findAnnotation<TaskProperty>() != null }
                .map { it.name to it.getter.call(this) }
                .toMap()
    }


}