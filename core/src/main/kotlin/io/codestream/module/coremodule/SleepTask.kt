package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext

class SleepTask : Task, TaskBinder {

    @TaskProperty
    var duration: Long = 0

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        Thread.sleep(duration)
        return done()
    }
}