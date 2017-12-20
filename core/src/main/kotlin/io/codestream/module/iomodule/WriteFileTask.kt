package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import java.nio.charset.Charset
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@TaskDescriptor("write", description = "Writes content to a file")
class WriteFileTask : Task {

    @TaskProperty(description = "Name of destination file")
    @get:NotBlank
    var file: String = ""

    @TaskProperty(description = "Content to write to the file")
    @get:NotBlank
    var content: String = ""

    @TaskProperty(description = "File encoding to use, default is 'UTF-8'")
    @get:NotBlank
    var encoding: String = "UTF-8"

    @TaskProperty(description = "Overwrite existing file if it exists, default is 'true'")
    @get:NotNull
    var overwrite: Boolean = true


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(file)
        if (!overwrite && theFile.exists()) {
            return invalidParameter(id, "$file exists and overwrite disabled")
        }
        if (theFile.isDirectory) {
            return invalidParameter(id, "$file is a directory")
        }
        theFile.writeText(content, Charset.forName(encoding))
        return done()
    }
}