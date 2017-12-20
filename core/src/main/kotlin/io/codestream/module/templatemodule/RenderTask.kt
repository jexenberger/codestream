package io.codestream.module.templatemodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.template.TemplateEngine
import javax.validation.constraints.NotBlank

@TaskDescriptor("render", description = "Renders a template")
class RenderTask : Task, SetOutput {

    @TaskProperty(description = "Path to template file located in templates basepath")
    @get:NotBlank
    var template: String = ""

    @TaskProperty(description = "Variable to set using rendered output, default is '\$templateOutput'")
    @get:NotBlank
    override var outputVar: String = "\$templateOutput"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        var output = TemplateEngine.render(template, ctx)
        ctx[outputVar] = output
        return done()
    }
}