package io.codestream.module.templatemodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.template.TemplateEngine
import javax.validation.constraints.NotBlank

@TaskDescriptor("basepath", description = "Sets the base folder for template (default is 'templates')")
class SetBasePath : Task {

    @TaskProperty(description = "New path to use which contains template files")
    @get:NotBlank
    var basePath: String = ""

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        TemplateEngine.reinit(basePath)
        return done()
    }
}