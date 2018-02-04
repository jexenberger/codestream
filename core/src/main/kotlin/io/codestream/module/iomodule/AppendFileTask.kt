package io.codestream.module.iomodule

import io.codestream.core.*
import java.io.File


@TaskDescriptor("append", description = "Appends content to an existing file")
class AppendFileTask : BaseWriteTask() {


    override fun performWrite(theFile: File, id: TaskId): TaskError? {
        if (!theFile.exists()) {
            return invalidParameter(id, "${theFile.name} does not exist")
        }
        theFile.appendText(content)
        return done()
    }

}