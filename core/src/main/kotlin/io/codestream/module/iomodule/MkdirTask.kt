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
        val result = File(dir).mkdirs()
        return if (result) done() else taskFailed(id, "Unable to create directory -> $dir")
    }
}