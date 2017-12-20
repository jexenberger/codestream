package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.Min

@TaskDescriptor("sleep", description = "Sleeps for a specified time")
class SleepTask : Task {

    @TaskProperty(description = "duration to sleep in milliseconds, default is '1000'")
    @Min(1)
    var duration: Long = 1000

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        Thread.sleep(duration)
        return done()
    }
}