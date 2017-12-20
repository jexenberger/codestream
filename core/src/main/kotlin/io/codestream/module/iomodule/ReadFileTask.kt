package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import java.nio.charset.Charset
import javax.validation.constraints.NotBlank

@TaskDescriptor("read", description = "Reads a file")
class ReadFileTask : Task {

    @TaskProperty(description = "File to read")
    @get:NotBlank
    var src: String = ""

    @TaskProperty(description = "Name of output variable to set with file content, default is '\$readFile'")
    @get:NotBlank
    var outputVar: String = "\$readFile"

    @TaskProperty(description = "Source file encoding, default is 'UTF-8'")
    @get:NotBlank
    var encoding: String = "UTF-8"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(src)
        if (!theFile.exists()) {
            return invalidParameter(id, "$src does not exist")
        }
        if (theFile.isDirectory) {
            return invalidParameter(id, "$src does is a directory")
        }
        if (!theFile.canRead()) {
            return invalidParameter(id, "$src does is not readable")
        }
        val text = theFile.readText(Charset.forName(encoding))
        ctx[outputVar] = text
        return done()
    }
}