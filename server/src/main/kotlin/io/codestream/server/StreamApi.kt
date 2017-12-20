package io.codestream.server

import io.codestream.core.TaskError
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.log.BufferedStreamLog
import io.codestream.util.log.RunLog
import io.codestream.util.ok
import java.io.File
import java.util.*

class StreamApi {

    fun runStream(path: String,
                  group: String,
                  name: String,
                  data: StreamRun): Either<StreamRun, TaskError> {
        val id = UUID.randomUUID().toString()
        val streamLog = BufferedStreamLog(path, group, name, id)
        val log = RunLog(streamLog, streamLog)
        val runtime = CodestreamRuntime.init(emptyArray())
        val ctx = StreamContext(log = log)
        data.id = id
        data.log = log
        val streamPath = "$path/$group/$name.yml"
        val result = runtime.runStream(File(streamPath), data.parameters, ctx)
        return result?.let { fail<StreamRun, TaskError>(it) } ?: ok(data)
    }




}