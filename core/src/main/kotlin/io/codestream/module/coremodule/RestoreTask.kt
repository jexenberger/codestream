package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.YamlFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class RestoreTask : Task {

    @TaskProperty()
    var file: String = ".\$\$run.yaml"

    @TaskProperty()
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