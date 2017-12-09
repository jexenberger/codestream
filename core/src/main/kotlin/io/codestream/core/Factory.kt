package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Either

interface Factory<T : Executable> {
    val documentation: TaskDocumentation
        get() = TaskDocumentation("name", "description")

    fun create(defn: ExecutableDefinition<T>, ctx: StreamContext, module: Module): Either<T, TaskError>
}