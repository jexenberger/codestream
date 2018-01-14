package io.codestream.runtime.streamimpl

import io.codestream.core.*
import io.codestream.runtime.Stream
import io.codestream.runtime.StreamContext
import io.codestream.runtime.YAMLStreamBuilder
import io.codestream.util.Either
import io.codestream.util.ok
import java.io.File

class StreamTaskFactory(streamFile: String) : Factory<StreamTask> {

    val stream: Stream

    init {
        stream = YAMLStreamBuilder(File(streamFile), false).build()
        stream.echo = false
    }


    override fun getBinding(parms: Map<String, Any?>): Binding<StreamTask> {
        return { id: TaskId, ctx: StreamContext, instance: StreamTask ->
            instance.bind(parms)
            done()
        }
    }


    override fun create(defn: ExecutableDefinition<StreamTask>, ctx: StreamContext, module: Module): Either<StreamTask, TaskError> {
        return ok(StreamTask(stream))
    }
}