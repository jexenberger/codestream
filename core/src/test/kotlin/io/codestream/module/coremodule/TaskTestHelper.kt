package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext


fun testId(module: String = "test", name: String = "test"): TaskId {
    return TaskId(module, name)
}

fun <T : Executable> createTaskContext(coreModule: Module = MockModule(),
                                       task: String = "<test>",
                                       bindingParams: Map<String, Any?> = mapOf(),
                                       condition: Conditional = defaultCondition()): Pair<StreamContext, ExecutableDefinition<T>> {
    val ctx = StreamContext()
    val taskType = coreModule.typeOf(task)
    val id = testId()
    val mapping = MapBinding(id, taskType, bindingParams).toBinding()
    val defn = ExecutableDefinition<T>(taskType, id, mapping, condition)
    return Pair(ctx, defn)
}