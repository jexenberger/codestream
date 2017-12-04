package io.codestream.core

import io.codestream.runtime.StreamContext

fun done() :TaskError? = null



interface Executable {

    fun bind(defn: ExecutableDefinition,
             ctx: StreamContext): TaskError?

}