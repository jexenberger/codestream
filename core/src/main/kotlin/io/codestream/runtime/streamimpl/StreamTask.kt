package io.codestream.runtime.streamimpl

import io.codestream.core.Task
import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.runtime.Stream
import io.codestream.runtime.StreamContext

class StreamTask(val stream: Stream) : Task {

    private var params: Map<String, Any?> = emptyMap()



    fun bind(parms: Map<String, Any?>) {
        this.params = parms
    }


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        //we don't need to process inputs or track so we run this stream inline and not via runtime
        val input = params.mapValues { ctx.evalTo<Any?>(it.value) }
        return stream.run(input, ctx)
    }
}