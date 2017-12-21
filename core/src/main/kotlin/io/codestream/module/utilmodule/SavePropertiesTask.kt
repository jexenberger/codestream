package io.codestream.module.utilmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.FileOutputStream
import java.time.LocalDateTime.now
import java.util.*

@TaskDescriptor("save-properties", description = "Saves a set of values to a properties file")
class SavePropertiesTask : Task {

    @TaskProperty(description = "Output properties file")
    var file: String = ""

    @TaskProperty(description = "Properties to set")
    var properties: Map<String, Any?> = mapOf()

    @TaskProperty(description = "Comment to include the in properties file")
    var comment: String = "Written by Codestream on ${now()}"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val propertiesFile = Properties()
        propertiesFile += ctx.evalTo<Map<String, Any?>?>(properties.mapValues {
            it.value?.toString()
        }) ?: emptyMap()
        propertiesFile.store(FileOutputStream(file), comment)
        return done()
    }
}