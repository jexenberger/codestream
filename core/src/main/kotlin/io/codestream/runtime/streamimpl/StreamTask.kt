package io.codestream.runtime.streamimpl

import io.codestream.core.Task
import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.runtime.Stream
import io.codestream.runtime.StreamContext

class StreamTask(val stream: Stream) : Task {

    private var params: Map<String, Any?> = emptyMap()

    init {

    }


    fun bind(parms: Map<String, Any?>) {
        this.params = parms
    }


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        return stream.run(params, ctx)
    }
}