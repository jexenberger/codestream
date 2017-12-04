package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext

class EchoTask : Task, TaskBinder {

    @TaskProperty
    var value: String? = null

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        ctx.log.log(value ?: "<null>")
        return done()
    }
}