package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.events.Event
import io.codestream.util.events.Events

class Stream constructor(val id: String,
                         val group: String,
                         val desc: String,
                         val runnables: Array<RunExecutable>,
                         val onError: ExecutableDefinition? = null,
                         val parameters: Map<String, Parameter> = mapOf()) {



    fun resolveInput(input: Map<String, Any?>): Map<String, Any?> {
        return input.mapValues {
            val parameter = parameters[it.key]
            parameter ?: throw InputError(it.key, "UnknownInputParameter", "'${it.key}' is not defined as an input parameter")
            if (parameter.required && it.value == null) {
                throw InputError(it.key, "RequiredInputParameter", "'${it.key}' is a required parameter")
            }

            val convertedValue = when (it.value) {
                is String -> parameter.fromString(it.value?.toString())
                else -> it.value
            }
            if (convertedValue != null && !parameter.isIn(convertedValue)) {
                throw InputError(it.key, "NotAllowedInputParameter", "'${it.key}' must one of '${parameter.allowedValuesList}'")
            }
            convertedValue
        }
    }


    fun run(input: Map<String, Any?> = mapOf(), ctx: StreamContext = StreamContext()): TaskError? {
        ctx += resolveInput(input)

        val defn = ExecutableDefinition(
                type = TaskType("core", "group"),
                id = TaskId(this.group, this.id)
        )
        val result = RunGroupTask(defn, this.runnables)
                .run(ctx)
                ?.let {
                    onError?.let {
                        RunTask(it).run(ctx)
                    }
                    it
                }
        Events.fire(Event(id = "StreamCompleted", ctx = ctx))
        return result
    }
}