package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Eval

typealias Conditional = (id: TaskId, ctx: StreamContext) -> Boolean
typealias Binding<T> = (id: TaskId, ctx: StreamContext, executable: T) -> TaskError?

fun defaultCondition(): Conditional = { _, _ -> true }
fun scriptCondition(script: String): Conditional {
    return { _, ctx -> Eval.eval(script, ctx) }
}

fun <T> emptyBinding(): Binding<T> = { _, _, _ -> null }

data class ExecutableDefinition<in T : Executable>(val type: TaskType,
                                                   val id: TaskId,
                                                   val binding: Binding<T>,
                                                   val condition: Conditional = defaultCondition(),
                                                   val lineNumber: Long = 0,
                                                   val source: String? = null,
                                                   val internalId: Int = nextId()) {


    val module: Module
        get() = Module[type.namespace] ?: throw TaskError(id, "InvalidModuleDefined", invalidModuleMsg())

    constructor(type: TaskType,
                id: TaskId,
                params: Map<String, Any?>,
                condition: Conditional = defaultCondition(),
                lineNumber: Long = 0,
                source: String? = null,
                internalId: Int = nextId()) : this(
            type, id, MapBinding(id, type, params).toBinding(), condition, lineNumber, source, internalId)


    fun invalidModuleMsg(): String {
        return when (lineNumber) {
            0L -> "'${type.namespace}' for task is not a recognized namespace"
            else -> "'${type.namespace}' for task defined at line $lineNumber is not a recognized namespace"
        }
    }

    companion object {
        private var id = 0

        @Synchronized
        fun nextId(): Int {
            return ++id
        }
    }


}