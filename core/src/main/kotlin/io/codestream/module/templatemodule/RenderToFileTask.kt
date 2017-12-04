package io.codestream.module.templatemodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.template.TemplateEngine
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.validation.constraints.NotBlank

class RenderToFileTask : Task, TaskBinder {

    @TaskProperty
    @get:NotBlank
    var template: String = ""

    @TaskProperty
    @get:NotBlank
    var target: String = ""

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val file = File(target)
        if (file.isDirectory) {
            return invalidParameter(id, "$target is directory")
        }
        try {
            val writer = FileWriter(file)
            TemplateEngine.render(template, writer, ctx)
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            return taskFailed(id, e.message!!)
        }
        return done()
    }
}