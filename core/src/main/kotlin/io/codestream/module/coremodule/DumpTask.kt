package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File

class DumpTask : Task, TaskBinder {

    @TaskProperty
    var file: String = ".\$\$run.yaml"

    @TaskProperty
    var overwrite: Boolean = true


    var written: Boolean = false

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(file)
        val write = (!theFile.exists() || (theFile.exists() && overwrite))
        if (write) {
            theFile.writeText(ctx.toYaml())
            written = true
        }
        return done()
    }
}