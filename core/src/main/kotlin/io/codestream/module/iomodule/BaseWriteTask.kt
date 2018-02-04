package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

abstract class BaseWriteTask : Task {

    @TaskProperty(description = "Name of destination file")
    @get:NotBlank
    var file: String = ""
    @TaskProperty(description = "Content to write to the file")
    @get:NotBlank
    var content: String = ""
    @TaskProperty(description = "File encoding to use, default is 'UTF-8'")
    @get:NotBlank
    var encoding: String = "UTF-8"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(file)
        if (theFile.isDirectory) {
            return invalidParameter(id, "$file is a directory")
        }
        return performWrite(theFile, id)
    }

    abstract fun performWrite(theFile: File, id: TaskId): TaskError?
}