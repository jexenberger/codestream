package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Either

interface Factory<out T : Executable> {
    val documentation: TaskDocumentation
        get() = TaskDocumentation("name", "description")

    fun create(defn: ExecutableDefinition, ctx: StreamContext, module: Module): Either<T, TaskError>
}