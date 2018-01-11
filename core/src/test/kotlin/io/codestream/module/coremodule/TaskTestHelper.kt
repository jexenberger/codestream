package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.runtime.classimpl.TaskBinder


fun testId(module: String = "test", name: String = "test"): TaskId {
    return TaskId(module, name, TaskType("test", "test"))
}

fun <T : Executable> createTaskContext(coreModule: Module = MockModule(),
                                       task: String = "<test>",
                                       bindingParams: Map<String, Any?> = mapOf(),
                                       condition: Conditional = defaultCondition()): Pair<StreamContext, ExecutableDefinition<T>> {
    val ctx = StreamContext()
    val taskType = coreModule.typeOf(task)
    val id = testId()
    val binder = { id: TaskId, ctx: StreamContext, instance: T ->
        TaskBinder.bind(id, instance, ctx, bindingParams)
    }
    val defn = ExecutableDefinition<T>(taskType, id, binder, condition)
    return Pair(ctx, defn)
}