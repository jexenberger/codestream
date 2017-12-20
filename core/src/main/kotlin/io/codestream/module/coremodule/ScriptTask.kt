package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.whenTrue
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern


@TaskDescriptor("js", description = "Runs a snippet of Javascript which evaluates against the context")
class ScriptTask : Task, SetOutput {

    @TaskProperty(description = "Script to run")
    @get:NotBlank
    var script: String = ""

    @TaskProperty(description = "Name of output variable from result of script run to set")
    override var outputVar: String = ""

    @TaskProperty(description = "Sets the scope set set the variable in either parent or current context, default is current context")
    @get:NotBlank
    @get:Pattern(regexp = "^(current|parent)")
    var scope: String = "current"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val result = ctx.evalScript<Any?>(script)
        outputVar.isNotBlank().whenTrue {
            val workingCtx = if (scope.equals("parent")) ctx.parent?.let { it } ?: ctx else ctx
            workingCtx[outputVar] = result
        }
        return done()
    }

}