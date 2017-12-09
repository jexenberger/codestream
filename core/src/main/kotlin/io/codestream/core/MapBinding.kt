package io.codestream.core

import io.codestream.runtime.StreamContext

class MapBinding(val id: TaskId, val type: TaskType, val params: Map<String, Any?>) {


    fun toBinding(): Binding<Executable> {
        val binding: Binding<Executable> = { id: TaskId, ctx: StreamContext, executable: Executable ->
            TaskBinder.bind(id, type, executable, ctx, params)
        }
        return binding
    }

}