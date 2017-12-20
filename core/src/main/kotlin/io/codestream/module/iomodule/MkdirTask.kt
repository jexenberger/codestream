package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

@TaskDescriptor("mkdirs", description = "Deletes a file or directory")
class MkdirTask : Task {


    @TaskProperty(description = "directory to create")
    @get:NotBlank
    var dir: String = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val result = File(dir).mkdirs()
        return if (result) done() else taskFailed(id, "Unable to create directory -> $dir")
    }
}