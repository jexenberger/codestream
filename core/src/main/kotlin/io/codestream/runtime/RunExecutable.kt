package io.codestream.runtime

import io.codestream.core.ExecutableDefinition
import io.codestream.core.RunExecutableState
import io.codestream.core.TaskError


interface RunExecutable {
    val defn: ExecutableDefinition
    val id: Int
    var state: RunExecutableState

    fun run(ctx: StreamContext): TaskError?

    companion object {
        private var taskCnt = 0

        @Synchronized
        fun nextId() = taskCnt++
    }
}