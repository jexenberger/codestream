package io.codestream.module.utilmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import java.util.*

@TaskDescriptor("load-properties", description = "Loads a set of values from a properties and populates the context")
class LoadPropertiesTask : Task {

    @TaskProperty(description = "Output properties file")
    var file: String = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(file)
        if (!theFile.exists() && !theFile.isFile) {
            return invalidParameter(id, "$file doesn't exist or is a directory")
        }
        val props = Properties()
        props.load(theFile.inputStream())
        props.forEach { t, u -> ctx[t.toString()] = u }
        return done()
    }
}