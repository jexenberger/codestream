package io.codestream.core

import io.codestream.doc.ExecutableDocumentation
import io.codestream.runtime.StreamContext
import io.codestream.util.Either

interface Factory<T : Executable> {
    val documentation: ExecutableDocumentation
        get() = ExecutableDocumentation("name", "description")

    fun getBinding(parms: Map<String, Any?>): Binding<T>

    fun create(defn: ExecutableDefinition<T>, ctx: StreamContext, module: Module): Either<T, TaskError>
}