package io.codestream.module.iomodule

import io.codestream.core.*
import java.io.File
import java.nio.charset.Charset
import javax.validation.constraints.NotNull

@TaskDescriptor("write", description = "Writes content to a file")
class WriteFileTask : BaseWriteTask() {

    @TaskProperty(description = "Overwrite existing file if it exists, default is 'true'")
    @get:NotNull
    var overwrite: Boolean = true


    override fun performWrite(theFile: File, id: TaskId): TaskError? {
        if (!overwrite && theFile.exists()) {
            return invalidParameter(id, "$file exists and overwrite disabled")
        }
        theFile.writeText(content, Charset.forName(encoding))
        return done()
    }
}