package io.codestream.module.templatemodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.template.TemplateEngine
import javax.validation.constraints.NotBlank

class RenderTask : Task, TaskBinder, SetOutput {

    @TaskProperty
    @get:NotBlank
    var template: String = ""

    @TaskProperty
    @get:NotBlank
    override var outputVar: String = "\$templateOutput"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        var output = TemplateEngine.render(template, ctx)
        ctx[outputVar] = output
        return done()
    }
}