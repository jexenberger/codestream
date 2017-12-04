package io.codestream.module.coremodule

import io.codestream.core.Conditional
import io.codestream.core.ExecutableDefinition
import io.codestream.core.Module
import io.codestream.core.TaskId
import io.codestream.runtime.StreamContext


fun createTaskContext(coreModule: Module,
                      task: String,
                      bindingParams: Map<String, Any?> = mapOf(),
                      condition: Conditional): Pair<StreamContext, ExecutableDefinition> {
    val ctx = StreamContext()
    val taskType = coreModule.typeOf(task)
    val id = TaskId("test", "test")
    val defn = ExecutableDefinition(taskType, id, condition, bindingParams)
    return Pair(ctx, defn)
}