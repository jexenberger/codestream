package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank

class CopyDirTask : Task {

    @TaskProperty(description = "Source directory to copy files from")
    @NotBlank
    var src: String = ""

    @TaskProperty(description = "Target directory to copy files to")
    @NotBlank
    var target: String = ""

    @TaskProperty(description = "Pattern to match files against (accepts dir glob patterns)")
    var pattern: String = "**/*.*"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {

        IOFunctions().copyDir("", "", "")
        return done()
    }
}