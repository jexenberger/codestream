package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.events.Event
import io.codestream.util.events.Events

class Stream(val id: String,
             val group: String,
             val desc: String,
             internal val runnables: Array<RunExecutable<*>>,
             private val onError: ExecutableDefinition<*>? = null,
             val parameters: Map<String, Parameter> = mapOf(),
             var echo: Boolean = false,
             private val before: ExecutableDefinition<*>? = null,
             private val after: ExecutableDefinition<*>? = null) {


    fun resolveInput(input: Map<String, Any?>): Map<String, Any?> {
        return parameters.mapValues {
            val parameter = input[it.key]
            if (parameter == null && it.value.required && it.value.defaultValue != null) {
                throw InputError(it.key, "RequiredInputParameter", "'${it.key}' is not defined as an input parameter")
            }

            val convertedValue = when (parameter) {
                null -> it.value.defaultValue
                is String -> it.value.fromString(parameter)
                else -> it.value
            }
            if (convertedValue != null && !it.value.isIn(convertedValue)) {
                throw InputError(it.key, "NotAllowedInputParameter", "'${it.key}' must one of '${it.value.values}'")
            }
            convertedValue
        }
    }


    fun run(input: Map<String, Any?> = mapOf(), ctx: StreamContext = StreamContext()): TaskError? {
        val resolveInput = resolveInput(input)
        ctx += ctx.evalTo<Map<String, Any?>>(resolveInput) ?: emptyMap()

        val type = TaskType("core", "group")
        val defn = ExecutableDefinition<GroupTask>(
                type = type,
                id = TaskId(this.group, this.id, type),
                binding = emptyBinding(),
                lineNumber = 0
        )
        before?.let { RunTask(it).run(ctx) }
        val result = RunGroupTask(defn, this.runnables, echo = this.echo).run(ctx)?.let {
            onError?.let {
                RunTask(it).run(ctx)
            }
            it
        }
        result ?: after?.let { RunTask(it).run(ctx) }
        Events.fire(Event(id = "StreamCompleted", ctx = ctx))
        return result
    }
}