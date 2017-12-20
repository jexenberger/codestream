package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

@TaskDescriptor("dump", description = "Dumps the content of the context to the specified file")
class DumpTask : Task {

    @TaskProperty(description = "Name of file to dump the Stream context to, default is '.\$\$run.yaml'")
    @get:NotBlank
    var file: String = ".\$\$run.yaml"

    @TaskProperty(description = "Overwrite if file already exists, default is 'true'")
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