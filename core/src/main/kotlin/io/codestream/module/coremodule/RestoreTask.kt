package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.YamlFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@TaskDescriptor("restore", description = "Restores the values from a yaml file")
class RestoreTask : Task {

    @TaskProperty(description = "File to load which contains dumped context, default is '.\$\$run.yaml'")
    var file: String = ".\$\$run.yaml"

    @TaskProperty(description = "Fails the task if the file is not present, default is 'false'")
    var failIfNotPresent: Boolean = false


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val file = File(file)
        if (!file.exists() && failIfNotPresent) {
            return taskFailed(id, "$file does not exist")
        }
        try {
            @Suppress("UNCHECKED_CAST")
            val dataMap = YamlFactory.yaml().load(FileInputStream(file)) as Map<String, Any?>
            StreamContext.fromMap(dataMap, ctx)
        } catch (e: IOException) {
            return taskFailed(id, e.message!!)
        }
        return done()
    }
}