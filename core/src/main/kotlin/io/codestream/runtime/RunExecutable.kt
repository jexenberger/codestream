package io.codestream.runtime

import io.codestream.core.Executable
import io.codestream.core.ExecutableDefinition
import io.codestream.core.TaskError


interface RunExecutable<in T : Executable> {
    val defn: ExecutableDefinition<T>
    val id: Int
    var state: RunExecutableState
    val echo: Boolean
    var parent: RunExecutable<*>?

    fun run(ctx: StreamContext): TaskError?

    val depthCnt: Int get() = parent?.let { it.depthCnt + 1 } ?: 0

    companion object {
        private var taskCnt = 0

        @Synchronized
        fun nextId() = taskCnt++
    }
}