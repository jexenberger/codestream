package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.IOException
import javax.validation.constraints.NotBlank

class CopyFileTask : Task {

    @TaskProperty(description = "Source file path")
    @get:NotBlank
    var src: String = ""

    @TaskProperty(description = "Target path")
    @get:NotBlank
    var target: String = ""

    @TaskProperty(description = "Overwrite existing files, default is true")
    var overwrite: Boolean = true

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        try {
            if (!IOFunctions().copy(src, target, overwrite)) {
                return taskFailed(id, "Unable to copy from '$src' to '$target'")
            }
            return done()
        } catch (io: IOException) {
            ctx.log.error(id, io.message!!, io)
            return taskFailed(id, io.message!!)
        }
    }
}