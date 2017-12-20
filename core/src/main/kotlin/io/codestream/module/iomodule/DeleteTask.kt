package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

@TaskDescriptor("delete", description = "Deletes a file or directory")
class DeleteTask : Task {

    @TaskProperty(description = "Path to delete")
    @get:NotBlank
    var src: String = ""

    @TaskProperty(description = "Recursively delete this path if it's a directory")
    var recursive: Boolean = true


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(src)
        if (recursive) {
            theFile.deleteRecursively()
        } else {
            theFile.delete()
        }
        return done()
    }
}