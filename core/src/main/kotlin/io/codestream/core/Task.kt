package io.codestream.core

import io.codestream.runtime.StreamContext

interface Task : Executable {

    fun execute(id: TaskId, ctx: StreamContext): TaskError?

}