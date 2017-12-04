package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Eval

typealias Conditional = (defn: ExecutableDefinition, ctx: StreamContext) -> Boolean

fun defaultCondition() :Conditional = {_,_ -> true}
fun scriptCondition(script:String) : Conditional {
    return { _, ctx -> Eval.eval(script, ctx)}
}

data class ExecutableDefinition(val type: TaskType,
                                val id: TaskId,
                                val condition: Conditional = defaultCondition(),
                                val bindingParams: Map<String, Any?> = mapOf(),
                                val lineNumber: Long = 0,
                                val internalId:Int = nextId()) {


    val module: Module
        get() = Module[type.namespace] ?: throw TaskError(id, "InvalidModuleDefined", invalidModuleMsg())


    fun invalidModuleMsg(): String {
        return when (lineNumber) {
            0L -> "'${type.namespace}' for task is not a recognized namespace"
            else -> "'${type.namespace}' for task defined at line $lineNumber is not a recognized namespace"
        }
    }

    companion object {
        private var id = 0

        @Synchronized
        fun nextId():Int {
            return ++id
        }
    }


}