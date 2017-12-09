package io.codestream.core

import io.codestream.runtime.StreamContext

interface Binder<out T : Executable> {
    fun bind(defn: ExecutableDefinition<T>,
             ctx: StreamContext): TaskError?


}