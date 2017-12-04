package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

class MkdirTask : Task, TaskBinder {


    @TaskProperty
    @get:NotBlank
    var dir: String = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        File(dir).mkdirs()
        return done()
    }
}