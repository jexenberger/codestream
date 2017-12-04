package io.codestream.module.templatemodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.template.TemplateEngine
import javax.validation.constraints.NotBlank

class SetBasePath : Task, TaskBinder {

    @TaskProperty
    @get:NotBlank
    var basePath: String = ""

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        TemplateEngine.reinit(basePath)
        return done()
    }
}