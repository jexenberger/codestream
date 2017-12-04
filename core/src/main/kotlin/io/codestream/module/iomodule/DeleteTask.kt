package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class DeleteTask : Task, TaskBinder {

    @TaskProperty
    @get:NotBlank
    var src: String = ""

    @TaskProperty
    @get:NotNull
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